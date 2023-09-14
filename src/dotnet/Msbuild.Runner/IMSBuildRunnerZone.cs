using JetBrains.Application.BuildScript.Application.Zones;
using JetBrains.Platform.RdFramework.Actions.Backend;
using JetBrains.ReSharper.Feature.Services.Daemon;
using JetBrains.ReSharper.Psi;
using JetBrains.ReSharper.Psi.CSharp;

namespace Msbuild.Runner;

[ZoneDefinition]
public interface IMSBuildRunnerZone : IPsiLanguageZone,
    IRequire<ILanguageCSharpZone>,
    IRequire<DaemonZone>,
    IRequire<IRdActionsBackendZone>
{
}
