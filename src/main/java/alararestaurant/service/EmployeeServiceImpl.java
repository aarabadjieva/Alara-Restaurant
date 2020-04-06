package alararestaurant.service;

import alararestaurant.domain.dtos.EmployeeImportDto;
import alararestaurant.domain.dtos.PositionImportDto;
import alararestaurant.domain.entities.Employee;
import alararestaurant.domain.entities.Position;
import alararestaurant.repository.EmployeeRepository;
import alararestaurant.repository.PositionRepository;
import alararestaurant.util.FileUtil;
import alararestaurant.util.ValidationUtil;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final static String EMPLOYEE_JSON_FILE_PATH = "C:\\Users\\lin\\Documents\\Programming\\6.Hibernate\\11.EXAM PREPARATION\\AlaraRestaurantNEW\\src\\main\\resources\\files\\employees.json";

    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;
    private final ModelMapper mapper;
    private final FileUtil fileUtil;
    private final ValidationUtil validator;
    private final Gson gson;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, PositionRepository positionRepository, ModelMapper mapper, FileUtil fileUtil, ValidationUtil validator, Gson gson) {
        this.employeeRepository = employeeRepository;
        this.positionRepository = positionRepository;
        this.mapper = mapper;
        this.fileUtil = fileUtil;
        this.validator = validator;
        this.gson = gson;
    }

    @Override
    public Boolean employeesAreImported() {
       return this.employeeRepository.count() > 0;
    }

    @Override
    public String readEmployeesJsonFile() throws IOException {
        return this.fileUtil.readFile(EMPLOYEE_JSON_FILE_PATH);
    }

    @Override
    public String importEmployees(String employees) throws IOException {

        employees = readEmployeesJsonFile();
        EmployeeImportDto[] employeeDtos = gson.fromJson(employees, EmployeeImportDto[].class);
        StringBuilder sb = new StringBuilder();
        for (EmployeeImportDto employeeDto : employeeDtos) {
            Position position = this.positionRepository.findByName(employeeDto.getName()).orElse(null);
            if (position==null){
                PositionImportDto positionImportDto = new PositionImportDto(employeeDto.getPosition());
                position = mapper.map(positionImportDto, Position.class);
                if (!validator.isValid(position)){
                    sb.append(validator.violations(position)).append(System.lineSeparator());
                    continue;
                }
            }

            Employee employee = mapper.map(employeeDto, Employee.class);
            employee.setPosition(position);
            if (!validator.isValid(employee)){
                sb.append(validator.violations(employee)).append(System.lineSeparator());
                continue;
            }
            positionRepository.saveAndFlush(position);
            employeeRepository.saveAndFlush(employee);
            sb.append(String.format("Record %s succesfully imported", employee.getName()))
                    .append(System.lineSeparator());
        }
        return sb.toString();
    }
}
