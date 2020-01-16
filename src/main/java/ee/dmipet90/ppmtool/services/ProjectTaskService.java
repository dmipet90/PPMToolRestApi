package ee.dmipet90.ppmtool.services;

import ee.dmipet90.ppmtool.domain.Backlog;
import ee.dmipet90.ppmtool.domain.Project;
import ee.dmipet90.ppmtool.domain.ProjectTask;
import ee.dmipet90.ppmtool.exceptions.ProjectNotFoundException;
import ee.dmipet90.ppmtool.repositories.BacklogRepository;
import ee.dmipet90.ppmtool.repositories.ProjectRepository;
import ee.dmipet90.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTaskService {
    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String username) {
        Backlog backlog = projectService.findProjectByIdentifier(projectIdentifier, username).getBacklog();
        projectTask.setBacklog(backlog);
        Integer backlogSequence = backlog.getPTSequence();
        backlogSequence++;
        backlog.setPTSequence(backlogSequence);
        projectTask.setProjectSequence(projectIdentifier + "-" + backlogSequence);
        projectTask.setProjectIdentifier(projectIdentifier);
        if (projectTask.getPriority() == null || projectTask.getPriority() == 0) {
            projectTask.setPriority(3);
        }
        if (projectTask.getStatus() == "" || projectTask.getStatus() == null) {
            projectTask.setStatus("toDo");
        }
        return projectTaskRepository.save(projectTask);
    }

    public Iterable<ProjectTask> findBacklogById(String id, String username) {
        projectService.findProjectByIdentifier(id, username);
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }

    public ProjectTask findPTByProjectSequence(String backlogId, String ptId, String username) {
        projectService.findProjectByIdentifier(backlogId, username);

        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(ptId);
        if (projectTask == null) {
            throw new ProjectNotFoundException("Project task with ID: '" + ptId + "' not found");
        }
        if (!projectTask.getProjectIdentifier().equals(backlogId)) {
            throw new ProjectNotFoundException("Project task '" + ptId + "' does not exist in project: '" + backlogId + "'");
        }
        return projectTask;
    }

    public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlogId, String ptId, String username) {
        ProjectTask projectTask = findPTByProjectSequence(backlogId, ptId, username);
        projectTask = updatedTask;
        return projectTaskRepository.save(projectTask);
    }

    public void deletePTBProjectSequence(String backlogId, String ptId, String username) {
        ProjectTask projectTask = findPTByProjectSequence(backlogId, ptId, username);
        projectTaskRepository.delete(projectTask);
    }
}
