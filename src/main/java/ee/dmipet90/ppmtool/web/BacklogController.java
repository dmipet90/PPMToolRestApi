package ee.dmipet90.ppmtool.web;

import ee.dmipet90.ppmtool.domain.Project;
import ee.dmipet90.ppmtool.domain.ProjectTask;
import ee.dmipet90.ppmtool.services.MapValidationErrorService;
import ee.dmipet90.ppmtool.services.ProjectTaskService;
import ee.dmipet90.ppmtool.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/backlog")
@CrossOrigin
public class BacklogController {

    @Autowired
    private ProjectTaskService projectTaskService;

    @Autowired
    private MapValidationErrorService mapValidationErrorService;

    @PostMapping("/{backlogId}")
    public ResponseEntity<?> addPTtoBaclog(@Valid @RequestBody ProjectTask projectTask,
                                           BindingResult result, @PathVariable String backlogId, Principal principal) {
        ResponseEntity<?> errMap = mapValidationErrorService.mapValidationService(result);
        if (errMap != null) return errMap;
        ProjectTask projectTask1 = projectTaskService.addProjectTask(backlogId, projectTask, principal.getName());
        return new ResponseEntity<ProjectTask>(projectTask1, HttpStatus.CREATED);
    }

    @GetMapping("/{backlogId}")
    public Iterable<ProjectTask> getProjectBacklog(@PathVariable String backlogId, Principal principal) {
        return projectTaskService.findBacklogById(backlogId, principal.getName());
    }

    @GetMapping("/{backlogId}/{ptId}")
    public ResponseEntity<?> getProjectTask(@PathVariable String backlogId, @PathVariable String ptId, Principal principal) {
        ProjectTask projectTask = projectTaskService.findPTByProjectSequence(backlogId, ptId, principal.getName());
        return new ResponseEntity<ProjectTask>(projectTask, HttpStatus.OK);
    }

    @PatchMapping("/{backlogId}/{ptId}")
    public ResponseEntity<?> updateProjectTask(@Valid @RequestBody ProjectTask projectTask, BindingResult result,
                                               @PathVariable String backlogId, @PathVariable String ptId, Principal principal) {
        ResponseEntity<?> errMap = mapValidationErrorService.mapValidationService(result);
        if (errMap != null) return errMap;
        ProjectTask updatedTask = projectTaskService.updateByProjectSequence(projectTask, backlogId, ptId, principal.getName());
        return new ResponseEntity<ProjectTask>(updatedTask, HttpStatus.OK);
    }

    @DeleteMapping("/{backlogId}/{ptId}")
    public ResponseEntity<?> deleteProjectTask(@PathVariable String backlogId, @PathVariable String ptId, Principal principal) {
        projectTaskService.deletePTBProjectSequence(backlogId, ptId, principal.getName());
        return new ResponseEntity<String>("Project task '"+ptId+"' was deleted successfully", HttpStatus.OK);
    }
}
