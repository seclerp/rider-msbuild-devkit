using JetBrains.Application.BuildScript.Application.Zones;
using JetBrains.Platform.RdFramework;
using JetBrains.ProjectModel;

namespace MSBuild.DevKit;

[ZoneMarker]
public class ZoneMarker : IRequire<IMsBuildDevKitZone>
{
}
