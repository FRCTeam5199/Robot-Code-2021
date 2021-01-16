package frc.drive;

public class Autos{
    //[row(stage)][column(DATA: 0 for x, 1 for y, 2 for speed, 3 for special move #)]
    //special move - -1 for none(just drive), -3 for reverse drive, 0-whatever for whichever action to do, -2 for terminate
    public static final double[][] testAuto = {
        //{row#c0, row#c1, row#c2, row#c3},
        {1,2, 1, -1}, //x, y, speed, special move //r0
        {0,4, 1, -1}, //r1
        {0,5, 1, -1}, //r2
        {2,7, 1, -1}, //r3
        {0,0, 0, 0},  //actual data in special command doesn't matter at all
        {0,0, 0, -2}  //actual data in terminator doesn't matter at all
    };

    public static final double[][] defaultAuto = {
        {0,0,0, 0},
        {0,1.2,0.4, -1}, //was 2.5
        {0,0,0, -2}
    };

    public static final double[][] runForwardAimShootAuto = {
        {0,0,0, 0},
        {0,0,0,5}, //spin up
        {0,1.2,0.4, -1}, //was 2.5
        {0,0,0,11}, //point turret
        {0,0,0,4}, //disable turret
        {0,0,0, 3}, //prep shoot
        {0,0,0, 2}, //shoot all

        {0,0,0, -2}
    };

    public static final double[][] driveForwardThenReverse = {
        {0,0,0, 0},
        //{0,4,0.4, -1}, //was 1.2
        {0,-5.2,0.4, -3},
        {0,0,0, -2}
    };

    public static final double[][] driveToRendezvousThenBackThenToTrench = {
        {0,0,0,0},

        {0,0,0,5}, //spin up shooter

        {0,0,0,8},
        {0.53, 1.67, 0.6, -1},
        {0,0,0, 6},
        {1.97, 2.9, 0.4, -1}, //move in to intake
        // {1.52, 2.5, 0.4, -3}, //move back
        // {1.95, 2.55, 0.3, -1}, //move forward again to intake
        {0.53, 1.67, 0.6, -3}, //move back
        {0,0,0, 7},

        {0,0,0,1}, //target a
        {0,0,0,4}, //target b

        {0,0,0, 3}, //prep shoot
        {0,0,0, 2}, //shoot all

        {0,0,0, 5},

        {0,0, 0.6, -3},
        {0,1,0, 6},
        {0.15,5, 0.5, -1},

        {0, 3.2, 0.6, -3}, //drive back
        {0,0,0, 7},

        {0,0,0,10}, //target a
        {0,0,0,4}, //target b

        {0,0,0, 3}, //prep shoot
        {0,0,0, 2}, //shoot all

        //{0,0, 0.3, -3},

        {0,0,0,-2}
    };

    public static final double[][] intakeOnlyAuto = {
        {0,0,0, 6},
        {0,2,0.4, -1},
        {0,0,0, 7},
        {0,0,0.4, -3},
        {0,0,0, 6},
        {0,2,0.4, -1},
        {0,0,0, 7},
        {0,0,0, -2}
    };

    public static final double[][] intakeTargetOnlyAuto = {
        {0,0,0, 6},
        {0,3.8,0.4, -1},
        {0,0,0, 1},
        {0,0,0, 4},
        {0,0,0, 7},
        {0,0,0, -2}
    };

    public static final double[][] intakeSpinupTargetShootAuto = { //intake 2 and shoot
        {0,0,0, 8},
        {0,0,0, 5}, //spinup shooter
        {0,0,0, 6}, //start intake
        {0,3.8,0.4, -1}, //move
        {0,0,0, 1}, //turn
        {0,0,0, 4}, //aim
        {0,0,0, 3}, //prep shoot
        {0,0,0, 2}, //shoot all
        {0,0,0, 7}, //stop intake
        {0,0,0, -2}
    };

    public static final double[][] runAimShootAutoRightmost = {
        //{0,0,0, 5},
        {0.8,1.3,0.3, -1}, //was 0, 2.5
        {0,0,0, 1}, //s-comm 1 = turn turret to 135
        {0,0,0, 4}, 
        {0,0,0, 3}, //s-comm 3 = setup shooter
        {0,0,0, 2}, //s-comm 2 = shoot all balls in robot
        {0,0,0, 0}, //take a break
        {0,0,0, 6},
        {0,0,0, -2}
    };

    public static final double[][] runAimShootTrenchAutoRightmost = {
        //{0,0,0, 5},
        {0.2,1.2,0.3, -1}, //was 0, 2.5
        {0,0,0, 1}, //s-comm 1 = turn turret to 135
        {0,0,0, 4}, 
        {0,0,0, 3}, //s-comm 3 = setup shooter
        {0,0,0, 2}, //s-comm 2 = shoot all balls in robot
        {0,0,0, 0}, //take a break
        {0,0,0, 6},
        {0,2.4,0.3, -1},
        {0,3.5,0.3, -1},
        {0,0,0, -2}
    };

    public static final double[][] spinupOnlyAuto = {
        {0,0,0, 5},
        {0,0,0, -2}
    };

    public static final double[][] aimOnlyAuto = {
        {0,0,0, 1}, //s-comm 1 = turn turret to 135
        {0,0,0, 4}, //s-comm 4 = disable turret
      //{0,0,0, 2}, //s-comm 2 = shoot all balls in robot
        {0,0,0, -2} //term
    };

    public static final double[][] shootOnlyAuto = {
        {0,0,0, 1}, //s-comm 1 = turn turret to 135
        {0,0,0, 4}, 
        {0,0,0, 3}, //s-comm 3 = setup shooter
        {0,0,0, 2}, //s-comm 2 = shoot all balls in robot
        {0,0,0, 0}, //take a break
        {0,2.5,0.25, -1}, //move 2.5 forward
        {0,0,0, -2} //term
    };

    public static final double[][] auto1 = {
        //{row#c0, row#c1, row#c2, row#c3},
        {-3.5, 1.3, 1, -1}, //x, y, speed, special move //r0
        {-7, 5.25, 1, -1}, //r1
        {-11.5, 8, 1, -1}, //r2
        {-10, 11, 1, -1},
        {0, 0, 1, -1},
        {0,0, 0, -2}  //actual data in terminator doesn't matter at all
    };

    public static final double[][] stealTwoAuto = {
        {0,0,0,8},
        {0,0,0,6},
        {0.14, 2.15, 0.4, -1},
        {0,0,0,5},
        {0, 0.85, 0.5, -3},
        {-0.21, 2.2, 0.4, -1},
        {-4, 0.73, 0.5, -3},
        {0,0,0,7},

        {0,0,0,1}, //target a
        {0,0,0,4}, //target b

        {0,0,0, 3}, //prep shoot
        {0,0,0, 2}, //shoot all

        {-1, 1.7, 0.7, -1},
        // {0,0,0,6},
        // {-3.5, 0.6, 0.3, -3},
        // {-3.5, 0.9, 0.3, -1},
        // {-3.8, 1.5, 0.3, -1},
        // {-3.5, 1.1, 0.3, -3},
        // {-3.35, 1.66, 0.3, -1},
        // {-3.5, 1.1, 0.3, -3},
        // {-3, 1.77, 0.3, -1},
        // {0,0,0,7},

        {0,0,0, -2}
    };

    public static final double[][] midAuto = {

    };

    public static final double[][] nothingAuto = {
        {0,0,0,0},
        {0,0,0, -2}
    };
}