package frc.selfdiagnostics;

import frc.misc.ISubsystem;

import java.util.HashMap;

/**
 * I. Am. Simply. VIIIIIBBBBIIIIINNNNGGGG
 */
public class IssueHandler {
    /**
     * Current reported issues. Each subsystem should only have 1 issue. This may cause an issue if issues arent
     * constantly being reported like they are supposed to. When an issue resolves, remove it and then the next issue
     * will replace it otherwise it will go away (at least it should)
     */
    public static final HashMap<ISubsystem, ISimpleIssue> issues = new HashMap<>();
}
