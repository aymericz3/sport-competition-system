package com.sports.app;

import com.sports.cli.Cli;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        new Cli(new Scanner(System.in)).run();
    }
}
