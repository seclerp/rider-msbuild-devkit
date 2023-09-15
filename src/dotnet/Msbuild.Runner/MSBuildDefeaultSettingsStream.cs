using System.IO;
using System.Reflection;
using JetBrains.Application;
using JetBrains.Application.Settings;
using JetBrains.Lifetimes;
using JetBrains.ReSharper.Feature.Services.LiveTemplates.Templates;
using JetBrains.ReSharper.LiveTemplates.Resources;

namespace Msbuild.Runner;

[ShellComponent]
public class MSBuildDefeaultSettingsStream : IHaveDefaultSettingsStream
{
    public MSBuildDefeaultSettingsStream()
    {
        TemplateImage.Register("MSBuildTaskImage", LiveTemplatesCSharpThemedIcons.ScopeCS.Id);
    }

    public Stream GetDefaultSettingsStream(Lifetime lifetime)
    {
        var stream = Assembly.GetExecutingAssembly().GetManifestResourceStream("Msbuild.Runner.FileTemplates.MSBuildFileTemplates.xml");
        lifetime.AddDispose(stream);
        return stream;
    }

    public string Name => "MSBuild File Templates";
}
