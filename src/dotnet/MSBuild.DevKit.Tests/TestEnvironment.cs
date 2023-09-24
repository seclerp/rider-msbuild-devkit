using JetBrains.Application.BuildScript.Application.Zones;
using JetBrains.ReSharper.TestFramework;
using JetBrains.TestFramework;
using JetBrains.TestFramework.Application.Zones;

namespace MSBuild.DevKit.Tests;

[ZoneDefinition]
public class MsBuildDevKitTestEnvironmentZone : ITestsEnvZone, IRequire<PsiFeatureTestZone>, IRequire<IMsBuildDevKitZone>
{
}

[ZoneMarker]
public class ZoneMarker : IRequire<MsBuildDevKitTestEnvironmentZone>
{
}

[SetUpFixture]
public class MsBuildDevKitTestsAssembly : ExtensionTestEnvironmentAssembly<MsBuildDevKitTestEnvironmentZone>
{
}
