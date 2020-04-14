package fr.craftyourmind.manager.packet;

import java.io.IOException;

import fr.craftyourmind.manager.command.AbsCYMCommand;
import fr.craftyourmind.manager.command.ICYMCommandData;
import fr.craftyourmind.manager.server.network.ByteBuffer;
import fr.craftyourmind.manager.util.CYMData;

public class DataCYMCommand extends CYMData {

	private ICYMCommandData cmdData;
	
	public DataCYMCommand() {}
	public DataCYMCommand(ICYMCommandData cmdData) {
		this.cmdData = cmdData;
	}

	@Override
	public void readPacketData(ByteBuffer input) throws IOException {
		cmdData = AbsCYMCommand.get(input.readInt());
		cmdData = cmdData.readPacketData(input);
	}

	@Override
	public void writePacketData(ByteBuffer output) throws IOException {
		cmdData.writePacketData(output);
	}

	@Override
	public void callEvent() {
		cmdData.receive(player);
	}

	private static int typedata = 0;
	public int getTypedata() {
		return typedata;
	}
	public void setTypedata(int typedata) {
		this.typedata = typedata;
	}
}
