using Task = Microsoft.Build.Utilities.Task;

namespace CSharpMSBuildLibraryProjectTemplate;

public class Task1 : Task
{
    public override bool Execute()
    {
        Log.LogMessage(MessageImportance.High, "Hello World from a custom MSBuild task");
        return true;
    }
}
