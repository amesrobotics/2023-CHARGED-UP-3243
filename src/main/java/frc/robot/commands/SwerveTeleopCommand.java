// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.DriveTrain.DriveConstants;
import frc.robot.JoyUtil;
import frc.robot.subsystems.DriveSubsystem;

public class SwerveTeleopCommand extends CommandBase {

  // <> subsystem
  private final DriveSubsystem m_DriveSubsystem;

  // <> driver joyutil
  private final JoyUtil controller;

  // <> driving needs to be reversed if on red alliance
  private final boolean reverse;

  /**
   * Creates a new SwerveTeleopCommand.
   */
  public SwerveTeleopCommand(DriveSubsystem subsystem, JoyUtil controller) {
    m_DriveSubsystem = subsystem;
    this.controller = controller;

    reverse = DriverStation.getAlliance() == DriverStation.Alliance.Red;

    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // <> drive the drivetrain with the controller's input
    m_DriveSubsystem.drive(controller.getLeftJoystickYWithAdjustments() * (reverse ? -1 : 1),
      controller.getLeftJoystickXWithAdjustments() * (reverse ? -1 : 1), controller.getRightJoystickXWithAdjustments(),
      controller.getRightBumper() != DriveConstants.kFieldRelative);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
