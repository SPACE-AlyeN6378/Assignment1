package com.dsinnovators;

public class TestPaper {
    private int paper_code;
    private String name;
    private int paper_number;
    private boolean has_practical;
    private int theory_marks;
    private int th_marks_obtained = 0;
    private int practical_marks;
    private int pr_marks_obtained = 0;
    private boolean test_passed = false;

    public static char boolToChar(boolean is_true) {
        if (is_true)
            return 'T';
        else
            return 'F';
    }

    public TestPaper() {};

    public TestPaper(int p_code, String sub_name, int p_no, char theory, char practical, int marks1, int marks2) {
        paper_code = p_code;
        name = sub_name;
        paper_number = p_no;

        if (theory == 'T') {
            if (practical == 'T')
                has_practical = true;
            else if (practical == 'F')
                has_practical = false;
            else {
                throw new RuntimeException("Invalid input");
            }
        }
        else {
            throw new RuntimeException("Error: Should have a theory class!");
        }

        theory_marks = marks1;
        if (!has_practical) {
            practical_marks = 0;
        } else {
            practical_marks = marks2;
        }
    }

    private static double get_percent(int marks_obtained, int total_marks) {
        if (marks_obtained > total_marks) {
            System.out.println(marks_obtained +" / "+ total_marks + " = "+((double) marks_obtained / (double) total_marks));
            throw new ArithmeticException("Mark obtained exceed total marks");
        }
        double result = ((double) marks_obtained / (double) total_marks)*100;
        result = Math.round(result);
        return result;
    }

    public void set_marks_th(int th_marks_input) {
        th_marks_obtained = th_marks_input;
    }

    public void set_marks_pr(int pr_marks_input) {
        if (!has_practical) {
            pr_marks_obtained = 0;
        } else {
            pr_marks_obtained = pr_marks_input;
        }
    }

    public void grade_check() {
        if (!has_practical) {
            if (th_marks_obtained > 0) {
                test_passed = get_percent(th_marks_obtained, theory_marks) >= 50.0;
            }
        }
        else {
            if (th_marks_obtained > 0 && pr_marks_obtained > 0) {
                test_passed = (get_percent(th_marks_obtained, theory_marks) >= 50.0)
                        && (get_percent(pr_marks_obtained, practical_marks) >= 50.0);
            }
        }
    }



    public int getPaper_code() { return paper_code; }
    public String getPaper_name() { return name; }
    public int getTheory_marks() { return theory_marks; }
    public int getPractical_marks() { return practical_marks; }
    public int getMarks_obtainedTheory() { return th_marks_obtained; }
    public int getMarks_obtainedPractical() { return pr_marks_obtained; }
    public boolean getPassed() { return test_passed; }
}
