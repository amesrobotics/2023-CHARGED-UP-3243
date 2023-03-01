// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.JoyUtil;
import frc.robot.subsystems.LegAnkleSubsystem;

public class WristCommand extends CommandBase {
  /**
   * Creates a new Wrist.
   */
  private final LegAnkleSubsystem m_subsystem;
  private final JoyUtil m_controller;

  public WristCommand(LegAnkleSubsystem subsystem, JoyUtil controller) {
    m_subsystem = subsystem;
    m_controller = controller;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // m_subsystem.moveByXYTheta(JoyUtil.posWithDeadzone( m_controller.getLeftX() ), JoyUtil.posWithDeadzone(
    // -m_controller.getLeftY() ), JoyUtil.posWithDeadzone( m_controller.getRightY() ), JoyUtil.posWithDeadzone(
    // -m_controller.getRightX()));

    /*m_subsystem.moveManualSetpoints(JoyUtil.posWithDeadzone(m_controller.getLeftX()) / 5,
      JoyUtil.posWithDeadzone(-m_controller.getLeftY()), JoyUtil.posWithDeadzone(m_controller.getRightY()),
      JoyUtil.posWithDeadzone(-m_controller.getRightX()));*/
    
    m_subsystem.moveByXYTheta(JoyUtil.posWithDeadzone(m_controller.getLeftX()) / 5,
      JoyUtil.posWithDeadzone(-m_controller.getLeftY()), JoyUtil.posWithDeadzone(m_controller.getRightY()),
      JoyUtil.posWithDeadzone(-m_controller.getRightX()));

    m_subsystem.deleteThis_doSetpoint = !m_controller.getAButton();
    //m_subsystem.resetRoll();
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
