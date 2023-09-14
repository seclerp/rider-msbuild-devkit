using JetBrains.Application.BuildScript.Application.Zones;
using JetBrains.Platform.RdFramework;
using JetBrains.ProjectModel;

namespace Msbuild.Runner;

[ZoneMarker]
public class ZoneMarker
    : IRequire<IMSBuildRunnerZone>, IRequire<IProjectModelZone>, IRequire<IRdFrameworkZone>
{
}
