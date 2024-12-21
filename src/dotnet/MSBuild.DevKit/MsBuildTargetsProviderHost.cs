using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using JetBrains.Application.Components;
using JetBrains.Application.Parts;
using JetBrains.Lifetimes;
using JetBrains.Platform.MsBuildHost.ProjectModel;
using JetBrains.ProjectModel;
using JetBrains.ProjectModel.ProjectsHost;
using JetBrains.ProjectModel.ProjectsHost.MsBuild;
using JetBrains.ProjectModel.ProjectsHost.SolutionHost;
using JetBrains.Rd.Tasks;
using JetBrains.ReSharper.Feature.Services.Protocol;
using JetBrains.ReSharper.Resources.Shell;
using MSBuild.DevKit.Rd;

namespace MSBuild.DevKit;

[SolutionComponent(Instantiation.ContainerAsyncPrimaryThread)]
public class MsBuildTargetsProviderHost
{
    private readonly ISolution _solution;
    private readonly ISolutionHost _solutionHost;

    public MsBuildTargetsProviderHost(ISolution solution)
    {
        _solution = solution;
        _solutionHost = solution.ProjectsHostContainer().GetComponent<ISolutionHost>();
        var model = solution.GetProtocolSolution().GetMsBuildRunnerModel();
        model.GetTargets.SetAsync(GetTargets);
    }

    private Task<List<MsBuildTargetInfo>> GetTargets(Lifetime lifetime, MsBuildProjectInfo projectInfo)
    {
        using (ReadLockCookie.Create())
        {
            var project = _solution.GetProjectByGuid(projectInfo.Id);

            var projectMark = project?.GetProjectMark();
            if (projectMark is null)
            {
                throw new NotImplementedException("Validation");
            }

            var projectHost = _solutionHost.GetProjectHost(projectMark) as MsBuildProjectHost;
            if (projectHost is null)
            {
                throw new NotImplementedException("Validation");
            }

            return Task.FromResult(projectHost.Session
                .GetProjectTargets(projectMark)
                .Select(target => new MsBuildTargetInfo(target.Name))
                .ToList());
        }
    }
}
