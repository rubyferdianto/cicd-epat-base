package sg.edu.nus.iss.d13revision.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import sg.edu.nus.iss.d13revision.models.Person;
import sg.edu.nus.iss.d13revision.services.PersonService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(PersonController.class)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonService personService;

    private List<Person> mockPersonList;

    @BeforeEach
    void setUp() {
        Person person1 = new Person("12345678", "John", "Doe");
        Person person2 = new Person("87654321", "Jane", "Smith");
        mockPersonList = Arrays.asList(person1, person2);
    }

    @Test
    void testIndex_ShouldReturnIndexPage() throws Exception {
        mockMvc.perform(get("/person/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    void testIndex_WithHomeUrl_ShouldReturnIndexPage() throws Exception {
        mockMvc.perform(get("/person/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    void testIndex_WithIndexUrl_ShouldReturnIndexPage() throws Exception {
        mockMvc.perform(get("/person/index"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    void testGetAllPersons_ShouldReturnJsonArray() throws Exception {
        when(personService.getPersons()).thenReturn(mockPersonList);

        mockMvc.perform(get("/person/testRetrieve"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[0].lastName", is("Doe")))
                .andExpect(jsonPath("$[1].firstName", is("Jane")))
                .andExpect(jsonPath("$[1].lastName", is("Smith")));

        verify(personService, times(1)).getPersons();
    }

    @Test
    void testGetAllPersons_ShouldCallServiceOnce() throws Exception {
        when(personService.getPersons()).thenReturn(mockPersonList);

        mockMvc.perform(get("/person/testRetrieve"))
                .andExpect(status().isOk());

        verify(personService, times(1)).getPersons();
    }

    @Test
    void testPersonList_ShouldReturnPersonListPage() throws Exception {
        when(personService.getPersons()).thenReturn(mockPersonList);

        mockMvc.perform(get("/person/personList"))
                .andExpect(status().isOk())
                .andExpect(view().name("personList"))
                .andExpect(model().attributeExists("persons"))
                .andExpect(model().attribute("persons", hasSize(2)));

        verify(personService, times(1)).getPersons();
    }

    @Test
    void testShowAddPersonPage_ShouldReturnAddPersonForm() throws Exception {
        mockMvc.perform(get("/person/addPerson"))
                .andExpect(status().isOk())
                .andExpect(view().name("addPerson"))
                .andExpect(model().attributeExists("personForm"));
    }

    @Test
    void testSavePerson_WithValidData_ShouldRedirectToPersonList() throws Exception {
        doNothing().when(personService).addPerson(any(Person.class));

        mockMvc.perform(post("/person/addPerson")
                .param("firstName", "Alice")
                .param("lastName", "Johnson"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/person/personList"));

        verify(personService, times(1)).addPerson(any(Person.class));
    }

    @Test
    void testSavePerson_WithEmptyFirstName_ShouldReturnAddPersonPage() throws Exception {
        mockMvc.perform(post("/person/addPerson")
                .param("firstName", "")
                .param("lastName", "Johnson"))
                .andExpect(status().isOk())
                .andExpect(view().name("addPerson"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(personService, never()).addPerson(any(Person.class));
    }

    @Test
    void testSavePerson_WithEmptyLastName_ShouldReturnAddPersonPage() throws Exception {
        mockMvc.perform(post("/person/addPerson")
                .param("firstName", "Alice")
                .param("lastName", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("addPerson"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(personService, never()).addPerson(any(Person.class));
    }

    @Test
    void testSavePerson_WithNullValues_ShouldReturnAddPersonPage() throws Exception {
        mockMvc.perform(post("/person/addPerson"))
                .andExpect(status().isOk())
                .andExpect(view().name("addPerson"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(personService, never()).addPerson(any(Person.class));
    }

    @Test
    void testPersonToEdit_ShouldReturnEditPersonPage() throws Exception {
        mockMvc.perform(post("/person/personToEdit")
                .param("id", "12345678")
                .param("firstName", "John")
                .param("lastName", "Doe"))
                .andExpect(status().isOk())
                .andExpect(view().name("editPerson"))
                .andExpect(model().attributeExists("per"));
    }

    @Test
    void testPersonEdit_ShouldUpdatePersonAndRedirect() throws Exception {
        doNothing().when(personService).updatePerson(any(Person.class));

        mockMvc.perform(post("/person/personEdit")
                .param("id", "12345678")
                .param("firstName", "John")
                .param("lastName", "Updated"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/person/personList"));

        verify(personService, times(1)).updatePerson(any(Person.class));
    }

    @Test
    void testPersonDelete_ShouldRemovePersonAndRedirect() throws Exception {
        doNothing().when(personService).removePerson(any(Person.class));

        mockMvc.perform(post("/person/personDelete")
                .param("id", "12345678")
                .param("firstName", "John")
                .param("lastName", "Doe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/person/personList"));

        verify(personService, times(1)).removePerson(any(Person.class));
    }

    @Test
    void testPersonList_WithEmptyList_ShouldReturnEmptyPersonList() throws Exception {
        when(personService.getPersons()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/person/personList"))
                .andExpect(status().isOk())
                .andExpect(view().name("personList"))
                .andExpect(model().attributeExists("persons"))
                .andExpect(model().attribute("persons", hasSize(0)));
    }

    @Test
    void testGetAllPersons_WithEmptyList_ShouldReturnEmptyJsonArray() throws Exception {
        when(personService.getPersons()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/person/testRetrieve"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
