package dm.taskproducer;

import dm.DecryptManager;
import dm.agent.AgentConclusion;
import dm.agent.AgentTask;
import dm.dictionary.Dictionary;
import dm.difficultylevel.DifficultyLevel;
import machine.EnigmaMachine;
import machine.Machine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static utill.Utility.factorial;

public class TaskProducer implements Runnable {

    protected BlockingQueue<Runnable> agentTaskQueue;
    private final Machine machine;
    private final String alphabet;
    private final int taskSize;
    private final DifficultyLevel difficulty;
    private final String textToDecipher;
    private final Dictionary dictionary;
    private final BlockingQueue<AgentConclusion> candidatesQueue;

    private List<List<Integer>> listOfAllPermutationsRotorsIDs = new ArrayList<>();
    DecryptManager dm;
    private int taskCounter = 0;

    public TaskProducer(DecryptManager dm, int taskSize, DifficultyLevel difficultyLevel, String textToDecipher) {
        this.dm = dm;
        this.agentTaskQueue = dm.getThreadPoolBlockingQueue();
        this.machine = dm.getEnigmaMachine();
        this.alphabet = machine.getAlphabet();
        this.taskSize = taskSize;
        this.difficulty = difficultyLevel;
        this.textToDecipher = textToDecipher;
        this.dictionary = dm.getDictionary();
        this.candidatesQueue = dm.getCandidatesQueue();
    }

    public void run() {

        // set the tasks and send them to the pool
        switch (difficulty) {
            case EASY:
                produceEasyTasks(machine.getInUseReflector().getId(), machine.getInUseRotorsIDs());
                break;
            case MEDIUM:
                produceMediumTasks(machine.getInUseRotorsIDs());
                break;
            case HARD:
                produceHardTasks();
                break;
            case IMPOSSIBLE:

                break;
        }
    }

    private void produceHardTasks() {

        //for (int i = 0; i < factorial(machine.getRotorsCount()); i++) {

        List<Integer> rotorsIDs = new ArrayList<>(machine.getInUseRotorsIDs());

        printAllRecursive(rotorsIDs.size(), rotorsIDs);
        System.out.println(listOfAllPermutationsRotorsIDs);

        //produceMediumTasks(rotorsIDs);
        //  }
    }

    private void produceMediumTasks(List<Integer> rotorsIDs) {

        int numOfReflectors = machine.getAvailableReflectorsLen();

        for (int i = 1; i <= numOfReflectors; i++) {
            produceEasyTasks(i, rotorsIDs);
        }
    }

    private void produceEasyTasks(int reflectorID, List<Integer> rotorsIDs) {

        List<Integer> currentWindowsOffsets = new ArrayList<>(Collections.nCopies(machine.getRotorsCount(), 0));

        boolean finishedAllTasks = false;

        // easy mode so rotors doesn't change.

        List<Integer> inUseRotorsIDs = rotorsIDs;
        // get the reflector
        int inUseReflectorID = reflectorID;
        List<Integer> nextWindowsOffsets;
        Machine copyOfMachine = new EnigmaMachine((EnigmaMachine) machine); // Clone!

        // set up first agentTask
        try {
            agentTaskQueue.put(new AgentTask(inUseRotorsIDs, new ArrayList<>(currentWindowsOffsets), inUseReflectorID,
                    copyOfMachine, taskSize, textToDecipher, dictionary, candidatesQueue, dm.isBruteForceActionCancelledProperty()));
        } catch (InterruptedException ignored) {
            //throw new RuntimeException(e);
        }

        while (!finishedAllTasks && !dm.isIsBruteForceActionCancelled()) {

            // first clone a machine to send to the agent
            copyOfMachine = new EnigmaMachine((EnigmaMachine) machine); // Clone!

            // the next window characters to set for the agent, based on last window characters
            nextWindowsOffsets = getNextWindowsOffsets(taskSize, currentWindowsOffsets);
            if (AllWindowsOffsetsAtBeginning(nextWindowsOffsets)) {
                finishedAllTasks = true;
                continue;
            }

            // replace current list with next list
            currentWindowsOffsets.clear();
            currentWindowsOffsets.addAll(nextWindowsOffsets);

            try {
                agentTaskQueue.put(new AgentTask(inUseRotorsIDs, nextWindowsOffsets, inUseReflectorID,
                        copyOfMachine, taskSize, textToDecipher, dictionary, candidatesQueue, dm.isBruteForceActionCancelledProperty()));
            } catch (InterruptedException ignored) {
                //e.printStackTrace();
            }
        }
    }

    public void printAllRecursive(
            int n, List<Integer> elements) {

        if (n == 1) {
            printArray(elements);
        } else {
            for (int i = 0; i < n - 1; i++) {
                printAllRecursive(n - 1, new ArrayList<>(elements));
                if (n % 2 == 0) {
                    Collections.swap(elements, i, n - 1);
                } else {
                    Collections.swap(elements, 0, n - 1);
                }
            }
            printAllRecursive(n - 1, new ArrayList<>(elements));
        }
    }

    public void printArray(List<Integer> elements) {
        listOfAllPermutationsRotorsIDs.add(elements);
    }

    private List<Integer> getNextWindowsOffsets(int taskSize, List<Integer> currentWindowsOffsets) {
        List<Integer> nextWindowsOffsets = new ArrayList<>(currentWindowsOffsets);

        for (int i = 0; i < taskSize; i++) {

            for (int j = 0; j < nextWindowsOffsets.size(); j++) {
                nextWindowsOffsets.set(j, rotateWindow(nextWindowsOffsets.get(j)));

                // check if it is needed to rotate next rotor
                if (nextWindowsOffsets.get(j) != 0) {
                    break;
                }
            }

            if (AllWindowsOffsetsAtBeginning(nextWindowsOffsets)) {
                return nextWindowsOffsets;
            }
        }
        return nextWindowsOffsets;
    }

    private int rotateWindow(Integer windowOffset) {
        return (windowOffset + 1 + alphabet.length()) % alphabet.length();
    }

    private boolean AllWindowsOffsetsAtBeginning(List<Integer> nextWindowsOffsets) {
        return nextWindowsOffsets.stream().allMatch(offset -> offset == 0);
    }
}
