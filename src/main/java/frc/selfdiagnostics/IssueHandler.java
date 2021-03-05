package frc.selfdiagnostics;

import frc.misc.ISubsystem;

import java.util.HashMap;

public class IssueHandler {
    public static final HashMap<ISubsystem, SimpleIssue> issues = new HashMap<>();
}
