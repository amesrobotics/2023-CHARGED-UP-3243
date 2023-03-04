// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveTrain.DriveConstants;
import frc.robot.FieldPosManager;

public class DriveSubsystem extends SubsystemBase {

  // <> create swerve modules
  private final SwerveModule m_frontLeft = new SwerveModule(DriveConstants.IDs.kFrontLeftDrivingCanId,
    DriveConstants.IDs.kFrontLeftTurningCanId, DriveConstants.ModuleOffsets.kFrontLeftOffset);

  private final SwerveModule m_frontRight = new SwerveModule(DriveConstants.IDs.kFrontRightDrivingCanId,
    DriveConstants.IDs.kFrontRightTurningCanId, DriveConstants.ModuleOffsets.kFrontRightOffset);

  private final SwerveModule m_rearLeft = new SwerveModule(DriveConstants.IDs.kRearLeftDrivingCanId,
    DriveConstants.IDs.kRearLeftTurningCanId, DriveConstants.ModuleOffsets.kBackLeftOffset);

  private final SwerveModule m_rearRight = new SwerveModule(DriveConstants.IDs.kRearRightDrivingCanId,
    DriveConstants.IDs.kRearRightTurningCanId, DriveConstants.ModuleOffsets.kBackRightOffset);

  // <> gyro
  private final IMUSubsystem m_imuSubsystem = new IMUSubsystem();

  // <> for keeping track of position
  private final FieldPosManager m_fieldPosManager;

  /**
   * Creates a new DriveSubsystem.
   */
  public DriveSubsystem(FieldPosManager fieldPosManager) {
    resetEncoders();
    m_fieldPosManager = fieldPosManager;
  }

  @Override
  public void periodic() {
    m_fieldPosManager.updateFieldPosWithSwerveData(new Pose2d(m_imuSubsystem.getDisplacement(), m_imuSubsystem.getYaw()));
  }

  /**
   * <>
   *
   * @return the pose
   */
  public Pose2d getPose() {
    return m_fieldPosManager.getRobotPose();
  }

  /**
   * <> drive the robot
   *
   * @param xSpeed        Speed of the robot in the x direction (forward).
   * @param ySpeed        Speed of the robot in the y direction (sideways).
   * @param rotationSpeed Angular rate of the robot.
   * @param fieldRelative Whether the provided x and y speeds are relative to the
   *                      field.
   */
  public void drive(double xSpeed, double ySpeed, double rotationSpeed, boolean fieldRelative) {
    // <> apply dampers defined in constants
    xSpeed *= DriveConstants.kDrivingSpeedDamper;
    ySpeed *= DriveConstants.kDrivingSpeedDamper;
    rotationSpeed *= DriveConstants.kAngularSpeedDamper;

    // <> convert passed in speeds to SwerveModuleState[]
    SwerveModuleState[] swerveModuleStates = DriveConstants.ChassisKinematics.kDriveKinematics.toSwerveModuleStates(
      fieldRelative ? ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rotationSpeed,
        getHeading()) : new ChassisSpeeds(xSpeed, ySpeed, rotationSpeed));

    // <> set desired wheel speeds
    setModuleStates(swerveModuleStates, false);
  }

  /**
   * <> set wheels into an x position to prevent movement
   */
  public void setX() {
    m_frontLeft.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(45)), true);
    m_frontRight.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(-45)), true);
    m_rearLeft.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(-45)), true);
    m_rearRight.setDesiredState(new SwerveModuleState(0, Rotation2d.fromDegrees(45)), true);
  }

  /**
   * <> set the swerve modules' desired states
   *
   * @param desiredStates the desired SwerveModule states
   */
  public void setModuleStates(SwerveModuleState[] desiredStates) {
    setModuleStates(desiredStates, true);
  }

  /**
   * <> set the swerve modules' desired states
   *
   * @param desiredStates        the desired SwerveModule states
   * @param allowLowSpeedTurning if the wheels should turn at low speeds
   */
  public void setModuleStates(SwerveModuleState[] desiredStates, boolean allowLowSpeedTurning) {
    // <> desaturate wheel speeds
    SwerveDriveKinematics.desaturateWheelSpeeds(desiredStates, DriveConstants.kMaxMetersPerSecond);

    // <> set the desired states
    if (!allowLowSpeedTurning && desiredStates[0].speedMetersPerSecond != 0) {
      m_frontLeft.setDesiredState(desiredStates[0], allowLowSpeedTurning);
    }
    if (!allowLowSpeedTurning && desiredStates[1].speedMetersPerSecond != 0) {
      m_frontRight.setDesiredState(desiredStates[1], allowLowSpeedTurning);
    }
    if (!allowLowSpeedTurning && desiredStates[2].speedMetersPerSecond != 0) {
      m_rearLeft.setDesiredState(desiredStates[2], allowLowSpeedTurning);
    }
    if (!allowLowSpeedTurning && desiredStates[3].speedMetersPerSecond != 0) {
      m_rearRight.setDesiredState(desiredStates[3], allowLowSpeedTurning);
    }
  }

  /**
   * <> reset the drive encoders
   */
  public void resetEncoders() {
    m_frontLeft.resetEncoders();
    m_rearLeft.resetEncoders();
    m_frontRight.resetEncoders();
    m_rearRight.resetEncoders();
  }

  /**
   * <>
   *
   * @return the robot's heading
   */
  public Rotation2d getHeading() {
    return m_fieldPosManager.getRobotPose().getRotation();
  }

  /**
   * <>
   *
   * @return if the motors are at an ok temperature (will return false if at unsafe temperatures)
   */
  public boolean getMotorsOkTemperature() {
    return !(m_frontLeft.isTooHot() || m_frontRight.isTooHot() || m_rearLeft.isTooHot() || m_rearRight.isTooHot());
  }

  /**
   * <>
   *
   * @return charge station level (see {@link IMUSubsystem}'s charge station level method
   * for more specific details
   */
  public Rotation2d getChargeLevel() {
    return m_imuSubsystem.getChargeLevel();
  }
}