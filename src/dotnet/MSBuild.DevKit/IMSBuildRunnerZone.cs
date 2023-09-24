using JetBrains.Application.BuildScript.Application.Zones;
using JetBrains.Platform.RdFramework.Actions.Backend;
using JetBrains.ProjectModel.ProjectsHost.SolutionHost;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;

namespace MSBuild.DevKit;

[ZoneDefinition]
public interface IMsBuildDevKitZone : IPsiLanguageZone,
    IRequire<ILanguageCSharpZone>,
    IRequire<DaemonZone>,
    IRequire<IRdActionsBackendZone>,
    IRequire<IHostSolutionZone>
{
}
