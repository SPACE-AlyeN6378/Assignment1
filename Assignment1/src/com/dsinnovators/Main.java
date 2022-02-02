package com.dsinnovators;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.IntStream;

public class Main {

    public static String boolToChar(boolean is_true) {
        if (is_true)
            return "T";
        else
            return "F";
    }

    public static void main(String[] args) throws IOException {

        // Subject code vs Subject name
        Map<Integer, String> subject_name = new HashMap<>();
        Stream<String> lines2 = Files.lines(Paths.get("subject_name.csv"));
        subject_name = lines2
                .map(x -> x.split(","))
                .collect(Collectors.toMap(
                        x -> Integer.parseInt(x[0]),
                        x -> x[1]));
        lines2.close();

        // Collecting student names and roll numbers
        ArrayList<Student> students = new ArrayList<>();

        Stream<String> rows = Files.lines(Paths.get("student_info.csv"));
        rows
                .map(x -> x.split(","))
                .forEach(x -> {
                    Student s1 = null;
                    try {
                        s1 = new Student(Integer.parseInt(x[0]), x[1], "paper_info.csv");
                        s1.set_marks("marks.csv");
                        s1.store_marks_to_map("subject_paper.csv");
                        s1.store_subjects_passed("subject_paper.csv");
                        students.add(s1);
                    } catch (IOException e) {
                        System.out.println("ERROR! File not found");
                        e.printStackTrace();
                    }

                });
        rows.close();

        try {
            FileWriter sheet1 = new FileWriter("result.csv");
            //String script = "";
            students.forEach(st -> {
                    String row = String.join(",", String.valueOf(st.getRoll_number()), st.getName(),
                            String.valueOf(st.get_total_marks()), boolToChar(st.getResult()));
                try {
                    sheet1.write(row + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            sheet1.close();
            System.out.println("Successfully wrote the results!");

            FileWriter sheet2 = new FileWriter("mark_sheet.csv");
            for (Student st: students) {
                SortedSet<Integer> subject_codes = new TreeSet<>(st.get_marks_obtained().keySet());
                for (int sub_code: subject_codes) {
                    String row = String.join(",",
                            String.valueOf(st.getRoll_number()),
                            String.valueOf(sub_code),
                            String.valueOf(st.get_marks_obtained().get(sub_code)),
                            boolToChar(st.getSubjects_passed().get(sub_code)));
                            //boolToChar(st.getResult()));
                    try {
                        sheet2.write(row + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            sheet2.close();
            System.out.println("Successfully wrote the marks sheet!");
        }
        catch (IOException e) {
            System.out.println("Write failed!");
            e.printStackTrace();
        }
    }
}
