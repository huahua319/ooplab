package com.example.othello;

@FunctionalInterface
public interface CoordinateParser {
    Position parse(String rawInput);
}
