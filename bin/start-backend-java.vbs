' Starts the backend JAR in a hidden window for unattended local use.
Set shell = CreateObject("WScript.Shell")
Set fileSystem = CreateObject("Scripting.FileSystemObject")
projectRoot = fileSystem.GetParentFolderName(fileSystem.GetParentFolderName(WScript.ScriptFullName))
shell.CurrentDirectory = projectRoot
cmd = """D:\chifan\java17\bin\java.exe"" -jar ""wuye-admin\target\wuye-admin.jar"""
shell.Run cmd, 0, False
