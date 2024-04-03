using System;
using System.Threading.Tasks;
using JetBrains.Application.Components;
using JetBrains.Lifetimes;
using JetBrains.ProjectModel;
using JetBrains.ProjectModel.ProjectsHost.SolutionHost;
using JetBrains.Rd.Tasks;
using JetBrains.ReSharper.Feature.Services.Protocol;
using JetBrains.ReSharper.Resources.Shell;
using Microsoft.Build.Definition;
using Microsoft.Build.Evaluation;
using Microsoft.Build.Locator;
using MSBuild.DevKit.Rd;

namespace MSBuild.DevKit;

[SolutionComponent]
public class MsBuildEvaluationHost
{
    private readonly ISolution _solution;
    private readonly ISolutionHost _solutionHost;

    public MsBuildEvaluationHost(ISolution solution)
    {
        _solution = solution;
        _solutionHost = solution.ProjectsHostContainer().GetComponent<ISolutionHost>();
        var model = _solution.GetProtocolSolution().GetMsBuildRunnerModel();
        model.Evaluate.SetAsync(EvaluateAsync);
        _solution.GetProtocolSolution().ActiveMsBuildPath.
    }

    private Task<MsBuildEvaluationResult> EvaluateAsync(Lifetime lifetime, MsBuildEvaluateRequest request)
    {
        using (ReadLockCookie.Create())
        {
            // while (!Debugger.IsAttached) { Thread.Sleep(100); }
            var project = _solution.GetProjectByGuid(request.ProjectId);
            if (project is null)
            {
                throw new NotImplementedException("Validation");
            }

            var projectPath = project.ProjectFile?.Location.FullPath;
            if (projectPath is null)
            {
                throw new NotImplementedException("Validation");
            }

            MSBuildLocator.RegisterMSBuildPath();

            var msbuildProject = Project.FromFile(projectPath, new ProjectOptions());

            MsBuildEvaluationResult? result = null;
            try
            {
                var evaluationResult = msbuildProject.ExpandString(request.Expression);
                result = new MsBuildEvaluationResult(true, evaluationResult, []);
            }
        }
    }
}
