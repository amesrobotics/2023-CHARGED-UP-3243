package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.XboxController;

/**
 * ++ we'll use this class to write methods that help us process joystick inputs
 * and will mostly be for drive train things, and will include things like:
 * deadzone functions and joystick input curves, and anything else we need.
 */
public final class JoyUtil extends XboxController {

  // ++ WE HAVE A BUNCH OF FUNCTIONS HERE, AND WE NEED TO APPLY THEM IN THE RIGHT ORDER
  // ++ check the "composeDriveJoyFunctions" method at the bottom to see the order this should be done in
  // ++ (((I'm not putting it here to avoid multiple versions of the "correct" order)))

  // <> these are used for low pass filter
  double prevFilteredX;
  double prevFilteredY;
  double prevFilteredR;

  /**
   * creates a new JoyUtil joystick.
   *
   * @param controllerID the ID of the controller
   */
  public JoyUtil(int controllerID) {
    super(controllerID);
  }

  public static double posWithDeadzone(double pos) {
    return MathUtil.applyDeadband(pos, Constants.Joysticks.deadZoneSize);
  }

  public static double lowPassFilter(double pos, double prevFilterJoy, double filterStrength) {
    // ++ this method smooths out the joystick input so
    // ++ "prevFilterJoy" is the previous output of this function
    return filterStrength * prevFilterJoy + (1 - filterStrength) * pos;
  }

  private static double rawCurve(double pos) {
    return Constants.Joysticks.aCoeff * (Math.pow(pos,
      Constants.Joysticks.firstPower)) + Constants.Joysticks.bCoeff * (Math.pow(pos, Constants.Joysticks.secondPower));
  }

  public static double joyCurve(double pos) {
    // <> apply curve
    double valueCurved = rawCurve(pos);

    // <> make possible to input small values
    double valueAdjusted = valueCurved - (valueCurved >= 0 ? rawCurve(Constants.Joysticks.deadZoneSize) : rawCurve(
      -Constants.Joysticks.deadZoneSize));

    if (Math.abs(pos) <= Constants.Joysticks.deadZoneSize) {
      valueAdjusted = 0;
    }

    return valueAdjusted;
  }

  public static double fastMode(double pos, double leftTrigger, double rightTrigger) {
    double adjustment =
      1 - (leftTrigger * Constants.Joysticks.slowModeMultiplier) + (rightTrigger * Constants.Joysticks.fastModeMaxMultiplier);

    return pos * adjustment;
  }

  public void rumbleLeft(double strength) {
    setRumble(RumbleType.kLeftRumble, strength);
  }
  //meah for mayor


  // ++ these are the methods called above ===================================================================

  public void rumbleRight(double strength) {
    setRumble(RumbleType.kRightRumble, strength);
  }

  public void stopRumbleLeft() {
    setRumble(RumbleType.kLeftRumble, 0.0);
  }

  public void stopRumbleRight() {
    setRumble(RumbleType.kRightRumble, 0.0);
  }

  public void stopBothRumble() {
    setRumble(RumbleType.kLeftRumble, 0.0);
    setRumble(RumbleType.kRightRumble, 0.0);
  }
  //meah for mayor

  public void zeroPreviousFiltered() {
    // ++ this zeroes all the previous filtered values
    prevFilteredX = 0.0;
    prevFilteredY = 0.0;
    prevFilteredR = 0.0;
  }

  /**
   * <> this was pain to edit from last year send help
   *
   * @return the value of the left joystick's y (assumed usage is for driving)
   */
  public double getLeftJoystickYWithAdjustments() {
    double rawJoyPos = getLeftY();
    double adjustedPos = composeDriveJoyFunctions(rawJoyPos);
    double filteredPos = lowPassFilter(adjustedPos, prevFilteredX, Constants.Joysticks.driveLowPassFilterStrength);

    prevFilteredX = filteredPos;
    return filteredPos;
  }

  public double getLeftJoystickXWithAdjustments() {
    double rawJoyPos = getLeftX();
    double adjustedPos = composeDriveJoyFunctions(rawJoyPos);
    double filteredPos = lowPassFilter(adjustedPos, prevFilteredY, Constants.Joysticks.driveLowPassFilterStrength);

    prevFilteredY = filteredPos;
    return filteredPos;
  }

  public double getRightJoystickXWithAdjustments() {
    double rawJoyPos = getRightX();
    double adjustedPos = composeDriveJoyFunctions(rawJoyPos);
    double filteredPos = lowPassFilter(adjustedPos, prevFilteredR, Constants.Joysticks.rotationLowPassFilterStrength);

    prevFilteredR = filteredPos;
    return filteredPos;
  }

  public double composeDriveJoyFunctions(double rawJoyPos) {
    // ++ IMPORTANT: please note that this function now shouldn't be called outside of this class-- this class used
    // to be
    // ++ just full of methods, but it's now a wrapper class

    /* ++ this method will compose all the previous joy functions, so
     * THIS WILL BE THE ONLY METHOD USED for adjusting the drive joysticks
     *
     * ++ we need to apply the methods in the RIGHT ORDER
     * we want to do:
     * - deadzone
     * - low pass filtering
     * - joy curve
     * - do fastmode stuff
     * - convert joystick range [-1, 1] to range of robot speed [-max speed, max speed]
     * - dampen the output w/ a multiplier
     * and the order might have to be changed as we add more functions,
     * but deadzone should probably stay first, and dampening should probably stay last
     */

    double withDead = posWithDeadzone(rawJoyPos);
    double withCurve = joyCurve(withDead);
    double withSpeedMode = fastMode(withCurve, getLeftTriggerAxis(), getRightTriggerAxis());
    double withDamper = withSpeedMode * Constants.DriveTrain.DriveConstants.kDrivingSpeedDamper;

    // ++ I decided to make separate variables for everything to make it a little more readable /\

    return withDamper;
  }


  /**
   * ++ gets the x component vector of the D-Pad
   *
   * @return D-Pad x component
   */
  public double getDPadX() {
    double pov = getPOV();

    return (pov != -1) ? Math.cos(Math.toRadians(pov - 90.0)) * Constants.Joysticks.dPadDamper : 0;
  }

  /**
   * ++ gets the y component of the D-Pad
   *
   * @return D-Pad y component
   */
  public double getDPadY() {
    double pov = getPOV();

    return (pov != -1) ? Math.sin(Math.toRadians(pov - 90.0)) * Constants.Joysticks.dPadDamper : 0;
  }
}