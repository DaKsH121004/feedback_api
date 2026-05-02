package com.feedback.feedback.services.impl;

import com.feedback.feedback.dto.AssignmentDto;
import com.feedback.feedback.dto.AssignmentRequest;
import com.feedback.feedback.dto.CourseDto;
import com.feedback.feedback.dto.Response;
import com.feedback.feedback.entities.Course;
import com.feedback.feedback.entities.Department;
import com.feedback.feedback.entities.Faculty;
import com.feedback.feedback.entities.FacultyCourseAssignment;
import com.feedback.feedback.exceptions.AlreadyExistException;
import com.feedback.feedback.exceptions.NotFoundException;
import com.feedback.feedback.repositories.CourseRepository;
import com.feedback.feedback.repositories.DepartmentRepository;
import com.feedback.feedback.repositories.FacultyCourseAssignmentRepository;
import com.feedback.feedback.repositories.FacultyRepository;
import com.feedback.feedback.services.FacultyCourseAssignmentService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class FacultyCourseAssignmentServiceImpl implements FacultyCourseAssignmentService {

    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final FacultyCourseAssignmentRepository assignmentRepository;

    @Override
    public Response createAssignment(AssignmentRequest request) {


        Faculty faculty = facultyRepository.findById(request.getFacultyId())
                .orElseThrow(() -> new NotFoundException("Faculty not found"));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new NotFoundException("Department not found"));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new NotFoundException("Course not found"));

        boolean belongs = faculty.getDepartments()
                .stream()
                .anyMatch(d -> d.getId().equals(request.getDepartmentId()));

        if (!belongs) {
            throw new NotFoundException("Faculty does not belong to this department");
        }

        boolean alreadyAssigned = assignmentRepository
                .existsByFacultyIdAndDepartmentIdAndCourseId(
                        request.getFacultyId(),
                        request.getDepartmentId(),
                        request.getCourseId()
                );

        if (alreadyAssigned) {
            throw new AlreadyExistException("Already Assigned");
        }


        FacultyCourseAssignment assignment = FacultyCourseAssignment.builder()
                .faculty(faculty)
                .department(department)
                .course(course)
                .semester(request.getSemester())
                .classSection(request.getClassSection())
                .build();

        assignmentRepository.save(assignment);

        return Response.builder()
                .status(201)
                .message("Assignment created successfully")
                .build();
    }

    @Override
    public Response updateAssignment(Long id, AssignmentRequest request) {

        FacultyCourseAssignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Assignment not found"));

        boolean alreadyAssigned = assignmentRepository
                .existsByFacultyIdAndDepartmentIdAndCourseId(
                        request.getFacultyId(),
                        request.getDepartmentId(),
                        request.getCourseId()
                );

        if (alreadyAssigned) {
            throw new AlreadyExistException("Already Assigned");
        }

        Faculty faculty = facultyRepository.findById(request.getFacultyId()).orElseThrow();
        Department department = departmentRepository.findById(request.getDepartmentId()).orElseThrow();
        Course course = courseRepository.findById(request.getCourseId()).orElseThrow();

        boolean belongs = faculty.getDepartments()
                .stream()
                .anyMatch(d -> d.getId().equals(request.getDepartmentId()));

        if (!belongs) {
            throw new NotFoundException("Faculty does not belong to this department");
        }

        assignment.setFaculty(faculty);
        assignment.setDepartment(department);
        assignment.setCourse(course);
        assignment.setSemester(request.getSemester());
        assignment.setClassSection(request.getClassSection());

        assignmentRepository.save(assignment);

        return Response.builder()
                .status(200)
                .message("Assignment updated successfully")
                .build();
    }

    @Override
    public Response deleteAssignment(Long id) {

        if (!assignmentRepository.existsById(id)) {
            throw new NotFoundException("Assignment not found");
        }

        assignmentRepository.deleteById(id);

        return Response.builder()
                .status(200)
                .message("Assignment deleted successfully")
                .build();
    }

    @Override
    public Response getAllAssignments() {

        List<FacultyCourseAssignment> list = assignmentRepository.findAll();

        List<AssignmentDto> dtoList = list.stream().map(a ->
                AssignmentDto.builder()
                        .id(a.getId())
                        .facultyId(a.getFaculty().getId())
                        .facultyName(a.getFaculty().getFacultyName())
                        .departmentId(a.getDepartment().getId())
                        .departmentName(a.getDepartment().getDepartmentName())
                        .courseId(a.getCourse().getId())
                        .courseName(a.getCourse().getCourseName())
                        .semester(a.getSemester())
                        .classSection(a.getClassSection())
                        .build()
        ).toList();

        return Response.builder()
                .status(200)
                .message("Assignments fetched successfully")
                .assignments(dtoList)
                .build();
    }

    @Override
    public Response processBulkUpload(MultipartFile file) {
        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                return Response.builder().status(400).message("The uploaded file is empty.").build();
            }

            int departmentIdx = -1;
            int courseNameIdx = -1;
            int facultyNameIdx = -1;
            int semesterIdx = -1;
            int sectionIdx = -1;

            for (Cell cell : headerRow) {
                if (cell.getCellType() == CellType.STRING) {
                    String header = cell.getStringCellValue().trim().toLowerCase().replaceAll("\\s", "");
                    if (header.contains("department") || header.equals("dept")) departmentIdx = cell.getColumnIndex();
                    if (header.contains("course")) courseNameIdx = cell.getColumnIndex();
                    if (header.contains("faculty")) facultyNameIdx = cell.getColumnIndex();
                    if (header.contains("semester") || header.equals("sem")) semesterIdx = cell.getColumnIndex();
                    if (header.contains("section") || header.equals("class")) sectionIdx = cell.getColumnIndex();
                }
            }

            if (departmentIdx == -1 || courseNameIdx == -1 || facultyNameIdx == -1) {
                return Response.builder().status(400).message("Invalid Excel template. Required columns: Course Name, Faculty Name, Department").build();
            }

            int successCount = 0;
            int alreadyAssignedCount = 0;
            java.util.Set<String> missingDepts = new java.util.HashSet<>();
            java.util.Set<String> missingCourses = new java.util.HashSet<>();
            java.util.Set<String> missingFaculties = new java.util.HashSet<>();

            // Optimization: Fetch all entities into maps for O(1) lookup
            java.util.Map<String, Department> deptMap = new java.util.TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            departmentRepository.findAll().forEach(d -> deptMap.put(d.getDepartmentName().trim(), d));

            java.util.Map<String, Course> courseMap = new java.util.TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            courseRepository.findAll().forEach(c -> courseMap.put(c.getCourseName().trim(), c));

            java.util.List<Faculty> allFacultiesList = facultyRepository.findAll();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String departmentName = getCellValue(row.getCell(departmentIdx)).trim();
                String courseName = getCellValue(row.getCell(courseNameIdx)).trim();
                String facultyNamesRaw = getCellValue(row.getCell(facultyNameIdx)).trim();

                if (departmentName.isEmpty() || courseName.isEmpty() || facultyNamesRaw.isEmpty()) continue;

                Department department = deptMap.get(departmentName);
                if (department == null) {
                    missingDepts.add(departmentName);
                    continue;
                }

                Course course = courseMap.get(courseName);
                if (course == null) {
                    missingCourses.add(courseName);
                    continue;
                }

                // Split by /, ,, &, or " and " to handle multiple faculty members in one cell
                String[] facultyParts = facultyNamesRaw.split("[,/&]|(?i)\\s+and\\s+");
                
                for (String fNameRaw : facultyParts) {
                    fNameRaw = fNameRaw.trim();
                    if (fNameRaw.isEmpty()) continue;
                    
                    Faculty faculty = findFacultyFuzzy(fNameRaw, allFacultiesList);
                    if (faculty == null) {
                        missingFaculties.add(fNameRaw);
                        continue;
                    }

                    // Check if faculty belongs to department, if not add it
                    if (faculty.getDepartments() == null) {
                        faculty.setDepartments(new java.util.ArrayList<>(java.util.List.of(department)));
                        facultyRepository.save(faculty);
                    } else if (faculty.getDepartments().stream().noneMatch(d -> d.getId().equals(department.getId()))) {
                        faculty.getDepartments().add(department);
                        facultyRepository.save(faculty);
                    }

                    Integer semesterVal = null;
                    if (semesterIdx != -1) {
                        try {
                            String s = getCellValue(row.getCell(semesterIdx));
                            if (!s.isEmpty()) semesterVal = Integer.parseInt(s.replaceAll("[^0-9]", ""));
                        } catch (Exception ignored) {}
                    }
                    
                    String sectionVal = "";
                    if (sectionIdx != -1) {
                        sectionVal = getCellValue(row.getCell(sectionIdx)).trim();
                    }

                    boolean alreadyAssigned = assignmentRepository.existsByFacultyIdAndDepartmentIdAndCourseIdAndSemesterAndClassSection(
                            faculty.getId(), department.getId(), course.getId(), semesterVal, sectionVal);
                    
                    if (!alreadyAssigned) {
                        FacultyCourseAssignment assignment = FacultyCourseAssignment.builder()
                                .faculty(faculty)
                                .department(department)
                                .course(course)
                                .semester(semesterVal)
                                .classSection(sectionVal)
                                .build();
                        assignmentRepository.save(assignment);
                        successCount++;
                    } else {
                        alreadyAssignedCount++;
                    }
                }
            }

            StringBuilder finalMessage = new StringBuilder();
            finalMessage.append("Bulk upload processed: ")
                    .append(successCount).append(" created, ")
                    .append(alreadyAssignedCount).append(" already existed. ");
            
            java.util.List<String> errors = new java.util.ArrayList<>();
            if (!missingDepts.isEmpty()) errors.add("Departments not found: [" + String.join(", ", missingDepts) + "]");
            if (!missingCourses.isEmpty()) errors.add("Courses not found: [" + String.join(", ", missingCourses) + "]");
            if (!missingFaculties.isEmpty()) errors.add("Faculty not found: [" + String.join(", ", missingFaculties) + "]");
            
            if (!errors.isEmpty()) {
                finalMessage.append("Skipped entries because: ").append(String.join(". ", errors)).append(".");
            }

            return Response.builder()
                    .status(200)
                    .message(finalMessage.toString())
                    .build();
        } catch (Exception e) {
            log.error("Error processing bulk upload", e);
            return Response.builder().status(500).message("Error processing file: " + e.getMessage()).build();
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue().trim();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    private Faculty findFacultyFuzzy(String rawName, java.util.List<Faculty> allFaculties) {
        String cleanName = cleanName(rawName);
        if (cleanName.isEmpty()) return null;

        // 1. Exact match after cleaning
        for (Faculty f : allFaculties) {
            if (cleanName(f.getFacultyName()).equals(cleanName)) {
                return f;
            }
        }
        
        // 2. Contains match (DB name contains Excel name or vice versa)
        for (Faculty f : allFaculties) {
            String dbCleanName = cleanName(f.getFacultyName());
            if (dbCleanName.isEmpty()) continue;
            
            if (dbCleanName.contains(cleanName) || cleanName.contains(dbCleanName)) {
                return f;
            }
        }
        
        // 3. Match by first word (First name matching) - AGGRESSIVE
        String[] excelParts = cleanName.split("\\s+");
        if (excelParts.length > 0) {
            String excelFirst = excelParts[0];
            for (Faculty f : allFaculties) {
                String dbCleanName = cleanName(f.getFacultyName());
                String[] dbParts = dbCleanName.split("\\s+");
                if (dbParts.length > 0) {
                    String dbFirst = dbParts[0];
                    // Very aggressive: if one starts with the other and length is decent
                    if (dbFirst.equals(excelFirst)) return f;
                    if (dbFirst.length() > 3 && excelFirst.length() > 3) {
                        if (dbFirst.startsWith(excelFirst) || excelFirst.startsWith(dbFirst)) {
                            return f;
                        }
                    }
                }
            }
        }
        
        return null;
    }

    private String cleanName(String name) {
        if (name == null) return "";
        String cleaned = name.toLowerCase();
        
        // Remove text in brackets e.g. (CDC), (SoB)
        cleaned = cleaned.replaceAll("\\(.*?\\)", "");
        
        // Remove punctuation/special characters
        cleaned = cleaned.replaceAll("[^a-z\\s]", " ");
        
        // Remove common prefixes
        cleaned = cleaned.replaceAll("\\b(prof|dr|mr|ms|mrs|er)\\b", "");
        
        // Normalize spaces
        return cleaned.trim().replaceAll("\\s+", " ");
    }
}
