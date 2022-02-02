package com.dsinnovators;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Student {

    private int roll_number;
    private String name;
    private Map<Integer, Integer> theory_scores = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> theory_scores_ob = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> practical_scores = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> practical_scores_ob = new HashMap<Integer, Integer>();
    Map<Integer, Integer> subject_paper = new HashMap<>();
    private Map<Integer, Boolean> papers_passed = new HashMap<>();
    private Map<Integer, Boolean> subjects_passed = new HashMap<>();
    private Map<Integer, Integer> marks_obtained = new HashMap<>();

    private boolean passed = true;

    public Student(int input_roll, String name_input, String subjects_file) throws IOException {
        roll_number = input_roll;
        name = name_input;

        Stream<String> lines = Files.lines(Paths.get(subjects_file));
        theory_scores = lines
                .map(x -> x.split(","))
                .filter(x -> x[3].equalsIgnoreCase("T"))
                .collect(Collectors.toMap(
                        x -> Integer.parseInt(x[0]),
                        x -> Integer.parseInt(x[5])
                ));
        lines.close();

        Stream<String> lines2 = Files.lines(Paths.get(subjects_file));
        practical_scores = lines2
                .map(x -> x.split(","))
                .filter(x -> x[4].equalsIgnoreCase("T"))
                .collect(Collectors.toMap(
                        x -> Integer.parseInt(x[0]),
                        x -> Integer.parseInt(x[6])
                ));
        lines2.close();
    }

    public void set_marks(String results_script) throws IOException {
        Stream<String> theory_rows = Files.lines(Paths.get(results_script));
        theory_rows
                .map(x -> x.split(","))
                .filter(x -> Integer.parseInt(x[0]) == roll_number && x[2].toCharArray()[0] == 'T' && x[3].toCharArray()[0] == 'F')
                .forEach( x -> {
                    // System.out.println(Arrays.toString(x));
                    theory_scores_ob.put(Integer.parseInt(x[1]), Integer.parseInt(x[4]));
                });

        Stream<String> practical_rows = Files.lines(Paths.get(results_script));
        practical_rows
                .map(x -> x.split(","))
                .filter(x -> Integer.parseInt(x[0]) == roll_number && x[2].toCharArray()[0] == 'F' && x[3].toCharArray()[0] == 'T')
                .forEach( x -> {
                    practical_scores_ob.put(Integer.parseInt(x[1]), Integer.parseInt(x[4]));
                });

        check();
    }

    private void check() {
        if (theory_scores_ob.size() == 0 || practical_scores_ob.size() == 0) {
            throw new RuntimeException("The scoreboard is empty");
        }
        for (int p_no: theory_scores.keySet()) {
            if (theory_scores_ob.get(p_no) > theory_scores.get(p_no)) {
                System.out.println(theory_scores_ob.get(p_no)+"/"+theory_scores.get(p_no));
                throw new ArithmeticException("The mark obtained exceed total marks.");
            }
            if (theory_scores_ob.get(p_no) < (theory_scores.get(p_no)/2)) {
                passed = false;
            }
        }
        for (int p_no: practical_scores.keySet()) {
            if (practical_scores_ob.get(p_no) > practical_scores.get(p_no)) {
                System.out.println(practical_scores_ob.get(p_no)+"/"+practical_scores.get(p_no));
                throw new ArithmeticException("The mark obtained exceed total marks.");
            }
            if (practical_scores_ob.get(p_no) < (practical_scores.get(p_no)/2)) {
                passed = false;
            }
        }
    } // Overall status

    public void store_marks_to_map(String subject_paper_file) throws IOException {

        // Paper code vs Subject code
        Map<Integer, Integer> subject_paper = new HashMap<>();
        Stream<String> lines = Files.lines(Paths.get(subject_paper_file));
        subject_paper = lines
                .map(x -> x.split(","))
                .collect(Collectors.toMap(
                        x -> Integer.parseInt(x[1]),
                        x -> Integer.parseInt(x[0])
                ));
        lines.close();

        for (int p_no: subject_paper.keySet()) {
            int subject_mark = 0;
            if (theory_scores_ob.containsKey(p_no) && practical_scores_ob.containsKey(p_no)) {
                subject_mark = theory_scores_ob.get(p_no) + practical_scores_ob.get(p_no);
            } else if (theory_scores_ob.containsKey(p_no) && !practical_scores_ob.containsKey(p_no)) {
                subject_mark = theory_scores_ob.get(p_no);
            }

            if (!marks_obtained.containsKey(subject_paper.get(p_no))) {
                marks_obtained.put(subject_paper.get(p_no), subject_mark);
            } else {
                marks_obtained.replace(subject_paper.get(p_no), marks_obtained.get(subject_paper.get(p_no)) + subject_mark);
            }
        }
    }

    public void store_subjects_passed(String subject_paper_file) throws IOException {
        // Paper code vs Subject code
        Map<Integer, Integer> subject_paper = new HashMap<>();
        Stream<String> lines = Files.lines(Paths.get(subject_paper_file));
        subject_paper = lines
                .map(x -> x.split(","))
                .collect(Collectors.toMap(
                        x -> Integer.parseInt(x[1]),
                        x -> Integer.parseInt(x[0])
                ));
        lines.close();

        for (int p_no: subject_paper.keySet()) {
            boolean passed = true;
            if (theory_scores_ob.containsKey(p_no) && practical_scores_ob.containsKey(p_no)) {
                passed = (theory_scores_ob.get(p_no) >= (theory_scores.get(p_no)/2))
                        && (practical_scores_ob.get(p_no) >= (practical_scores.get(p_no)/2));
            } else if (theory_scores_ob.containsKey(p_no) && !practical_scores_ob.containsKey(p_no)) {
                passed = (theory_scores_ob.get(p_no) >= (theory_scores.get(p_no)/2));
            }

            if (!subjects_passed.containsKey(subject_paper.get(p_no))) {
                subjects_passed.put(subject_paper.get(p_no), passed);
            } else {
                subjects_passed.replace(subject_paper.get(p_no), subjects_passed.get(subject_paper.get(p_no)) && passed);
            }
        }
    } // Status for each subject

    public void print_status() {
        System.out.println();
        System.out.println("Name: "+name+" ("+roll_number+")");
        System.out.println(theory_scores_ob);
        System.out.println(practical_scores_ob);
        System.out.println(marks_obtained);
        System.out.println(passed ? "Passed!" : "Did not pass");
        System.out.println();
    }

    public int get_total_marks() {
        if (theory_scores_ob.size() == 0 || practical_scores_ob.size() == 0) {
            throw new RuntimeException("The scoreboard is empty");
        }
        Integer sum1 = theory_scores_ob.values().stream().mapToInt(Integer::valueOf).sum();
        Integer sum2 = practical_scores_ob.values().stream().mapToInt(Integer::valueOf).sum();
        return sum1 + sum2;
    }

    public Map<Integer, Integer> get_marks_obtained() { return marks_obtained; };
    public Map<Integer, Boolean> getSubjects_passed() { return subjects_passed; };
    public int getRoll_number() { return roll_number; }
    public String getName() { return name; }
    public boolean getResult() { return passed; }

    // public void determine() {};



}
