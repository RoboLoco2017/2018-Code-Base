//Package for all of our code.
package org.usfirst.frc.team5338.robot.subsystems;

//Import all needed classes from our code.
import org.usfirst.frc.team5338.robot.commands.CubeDetection;

//Import all needed classes from WPILib.
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionSystem extends Subsystem
{
	private static final NetworkTableInstance instance = NetworkTableInstance.create();
	private static NetworkTable table;
	
	public VisionSystem()
	{
		super();
		VisionSystem.instance.startServer("/tmp/networktables.persist", "0.0.0.0", 5800);
		VisionSystem.table = VisionSystem.instance.getTable("vision");
	}
	@Override
	public void initDefaultCommand()
	{
		this.setDefaultCommand(new CubeDetection());
	}
	@SuppressWarnings("static-method")
	public void track()
	{
		SmartDashboard.putBoolean("Is Connected", VisionSystem.instance.isConnected());
		SmartDashboard.putNumber("X Coord", VisionSystem.table.getEntry("x").getDouble(-1.0));
	}
}