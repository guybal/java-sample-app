# VS Code WSL Setup Guide

Your Java 21 is installed in WSL, but VS Code is running in Windows mode. Here are your options:

## Option 1: Use VS Code in WSL Mode (Recommended)

This is the **best solution** - it will allow VS Code to run directly in WSL and access Java 21 natively.

### Steps:

1. **Install Remote-WSL Extension:**
   - Open VS Code
   - Go to Extensions (Ctrl+Shift+X)
   - Search for "Remote - WSL"
   - Install it

2. **Connect to WSL:**
   - Press `Ctrl+Shift+P` (or `Cmd+Shift+P` on Mac)
   - Type: `Remote-WSL: Reopen Folder in WSL`
   - Select it
   - VS Code will reload and connect to WSL

3. **Verify:**
   - You should see "WSL: Ubuntu-22.04" in the bottom-left corner of VS Code
   - The Java extension will now see Java 21 in WSL

4. **Run your configurations:**
   - Press `F5`
   - Select "Spring Boot (Dev Profile - H2)" or "Spring Boot (Prod Profile - PostgreSQL)"
   - It should work now!

## Option 2: Install Java 21 on Windows

If you prefer to keep VS Code in Windows mode:

1. Download Java 21 for Windows:
   - Go to: https://adoptium.net/
   - Download OpenJDK 21 for Windows
   - Install it

2. Update VS Code settings:
   - Open `.vscode/settings.json`
   - Update the Java path to your Windows Java 21 installation:
     ```json
     {
         "java.jdt.ls.java.home": "C:\\Program Files\\Eclipse Adoptium\\jdk-21.0.x",
         "java.configuration.runtimes": [
             {
                 "name": "JavaSE-21",
                 "path": "C:\\Program Files\\Eclipse Adoptium\\jdk-21.0.x",
                 "default": true
             }
         ]
     }
     ```

3. Update launch.json:
   - Remove `javaHome` from launch configurations (or set to Windows path)
   - VS Code will use the Windows Java installation

## Option 3: Use Tasks to Build/Run (Hybrid Approach)

Keep VS Code in Windows but build/run via WSL tasks:

1. Use the existing tasks in `.vscode/tasks.json` (already configured for WSL)
2. Build from terminal: `mvn clean install` in WSL
3. Run via WSL directly or use the attach debugger configurations

## Recommendation

**Use Option 1** - It's the cleanest solution and ensures everything runs in the same environment (WSL). Your project files are already accessible from WSL via `/mnt/c/Users/GuyBalmas/Documents/Projects/guybal/java-sample-app`.

## After Setting Up WSL Mode

Once VS Code is in WSL mode:
- All Java tooling will use WSL Java 21
- Maven commands will use WSL Maven
- The launch configurations will work with Java 21
- No path issues or compatibility problems

## Verify It's Working

After connecting to WSL:
1. Check VS Code status bar (bottom-left): Should show "WSL: Ubuntu-22.04"
2. Press `Ctrl+Shift+P` → `Java: Configure Java Runtime` → Should show Java 21
3. Try running a launch configuration - should work without errors!

