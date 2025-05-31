using System.IO;
using System.Reflection;
using JetBrains.Application.Settings;
using JetBrains.Lifetimes;
using JetBrains.ReSharper.Feature.Services.LiveTemplates.Settings;
using JetBrains.ReSharper.Feature.Services.LiveTemplates.Templates;
using JetBrains.ReSharper.LiveTemplates.Resources;

namespace MSBuild.DevKit;

[DefaultSettings(typeof(LiveTemplatesSettings))]
public class MsBuildDefaultSettingsStream : IHaveDefaultSettingsStream
{
    public MsBuildDefaultSettingsStream()
    {
        TemplateImage.Register("MSBuildTaskImage", LiveTemplatesCSharpThemedIcons.ScopeCS.Id);
    }

    public Stream GetDefaultSettingsStream(Lifetime lifetime)
    {
        var stream = Assembly.GetExecutingAssembly().GetManifestResourceStream("MSBuild.DevKit.FileTemplates.MSBuildFileTemplates.xml");
        lifetime.AddDispose(stream);
        return stream;
    }

    public string Name => "MSBuild File Templates";
}
