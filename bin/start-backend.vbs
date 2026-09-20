' Calls the shared backend launcher without leaving a console window open.
Set shell = CreateObject("WScript.Shell")
Set fileSystem = CreateObject("Scripting.FileSystemObject")
projectRoot = fileSystem.GetParentFolderName(fileSystem.GetParentFolderName(WScript.ScriptFullName))
shell.Run "cmd /c """ & projectRoot & "\bin\run-backend.cmd""", 0, False
