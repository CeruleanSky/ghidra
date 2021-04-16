/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package agent.gdb;

import java.util.concurrent.CompletableFuture;

import agent.gdb.gadp.GdbLocalDebuggerModelFactory;
import agent.gdb.manager.GdbManager;
import agent.gdb.model.impl.GdbModelImpl;
import ghidra.dbg.DebuggerObjectModel;
import ghidra.dbg.LocalDebuggerModelFactory;
import ghidra.dbg.util.ConfigurableFactory.FactoryDescription;
import ghidra.util.classfinder.ExtensionPointProperties;

/**
 * Note this is in the testing source because it's not meant to be shipped in the release.... That
 * may change if it proves stable, though, no?
 */
@FactoryDescription( //
		brief = "IN-VM GNU gdb local debugger", //
		htmlDetails = "Launch a GDB session in this same JVM" //
)
@ExtensionPointProperties(priority = 80)
public class GdbInJvmDebuggerModelFactory implements LocalDebuggerModelFactory {

	private String gdbCmd = GdbManager.DEFAULT_GDB_CMD;
	@FactoryOption("GDB launch command")
	public final Property<String> gdbCommandOption =
		Property.fromAccessors(String.class, this::getGdbCommand, this::setGdbCommand);

	private boolean existing = false;
	@FactoryOption("Use existing session via new-ui")
	public final Property<Boolean> useExistingOption =
		Property.fromAccessors(boolean.class, this::isUseExisting, this::setUseExisting);

	@Override
	public CompletableFuture<? extends DebuggerObjectModel> build() {
		GdbModelImpl model = new GdbModelImpl();
		return model.startGDB(gdbCmd, new String[] {}).thenApply(__ -> model);
	}

	@Override
	public boolean isCompatible() {
		return GdbLocalDebuggerModelFactory.checkGdbPresent(gdbCmd);
	}

	public String getGdbCommand() {
		return gdbCmd;
	}

	public void setGdbCommand(String gdbCmd) {
		this.gdbCmd = gdbCmd;
	}

	public boolean isUseExisting() {
		return existing;
	}

	public void setUseExisting(boolean existing) {
		this.existing = existing;
		gdbCommandOption.setEnabled(!existing);
	}
}
