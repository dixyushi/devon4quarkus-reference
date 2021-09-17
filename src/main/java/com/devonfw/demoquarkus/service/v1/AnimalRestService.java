package com.devonfw.demoquarkus.service.v1;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.devonfw.demoquarkus.logic.UcFindAnimal;
import com.devonfw.demoquarkus.logic.UcManageAnimal;
import com.devonfw.demoquarkus.service.v1.model.AnimalDto;
import com.devonfw.demoquarkus.service.v1.model.AnimalSearchCriteriaDto;
import com.devonfw.demoquarkus.service.v1.model.NewAnimalDto;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.springframework.data.domain.PageImpl;
import org.tkit.quarkus.rs.models.PageResultDTO;

//In Quarkus all JAX-RS resources are treated as CDI beans
//default is Singleton scope
@Path("/animal/v1/")
// how we serialize response
@Produces(MediaType.APPLICATION_JSON)
// how we deserialize params
@Consumes(MediaType.APPLICATION_JSON)
public class AnimalRestService {

    // using @Context we can inject contextual info from JAXRS(e.g. http request, current uri info, endpoint info...)
    @Context
    UriInfo uriInfo;

    @Inject
    UcFindAnimal ucFindAnimal;

    @Inject
    UcManageAnimal ucManageAnimal;

    @APIResponses({
    @APIResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = PagedAnimalResponse.class))),
    @APIResponse(responseCode = "500")})
    @Operation(operationId = "Get Animals", description = "Returns list of animals matching given criteria, uses pagination")
    @GET
    // REST service methods should not declare exceptions, any thrown error will be transformed by exceptionMapper in
    // tkit-rest
    // We did not define custom @Path - so it will use class level path
    public PageImpl<AnimalDto> getAll(@BeanParam AnimalSearchCriteriaDto dto) {
        return (PageImpl) ucFindAnimal.findAnimals(dto);
    }

       @GET
    @Path("/search")
    public PageImpl<AnimalDto> getAllCriteriaApi(@BeanParam AnimalSearchCriteriaDto dto) {
        return (PageImpl) ucFindAnimal.findAnimalsByCriteriaApi(dto);
    }
 
    @GET
    @Path("/search/query")
    public PageImpl<AnimalDto> getAllQueryDsl(@BeanParam AnimalSearchCriteriaDto dto) {
        return (PageImpl) ucFindAnimal.findAnimalsByQueryDsl(dto);
    }
 
    @GET
    @Path("/search/name")
    public PageImpl<AnimalDto> getAllQuery(@BeanParam AnimalSearchCriteriaDto dto) {
        return (PageImpl) ucFindAnimal.findAnimalsByNameQuery(dto);
    }
 
    @GET
    @Path("/search/nativename")
    public PageImpl<AnimalDto> getAllNativeQuery(@BeanParam AnimalSearchCriteriaDto dto) {
        return (PageImpl) ucFindAnimal.findAnimalsByNameNativeQuery(dto);
    }
 
    @GET
    @Path("/search/nameorder")
    public PageImpl<AnimalDto> getAllOrderedByName() {
        return (PageImpl) ucFindAnimal.findAnimalsOrderedByName();
    }

    @APIResponses({
    @APIResponse(responseCode = "201", description = "OK, New animal created", content = @Content(schema = @Schema(implementation = NewAnimalDto.class))),
    @APIResponse(responseCode = "400", description = "Client side error, invalid request"),
    @APIResponse(responseCode = "500")})
    @Operation(operationId = "createNewAnimal", description = "Stores new animal in DB")
    @POST
    // We did not define custom @Path - so it will use class level path.
    // Although we now have 2 methods with same path, it is ok, because it is a different method (get vs post)
    public AnimalDto createNewAnimal(NewAnimalDto dto) {
        return ucManageAnimal.saveAnimal(dto);
    }

    @APIResponses({
    @APIResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = AnimalDto.class))),
    @APIResponse(responseCode = "404", description = "Animal not found"), @APIResponse(responseCode = "500")})
    @Operation(operationId = "getAnimalById", description = "Returns animal with given id")
    @GET
    @Path("{id}")
    public AnimalDto getAnimalById(@Parameter(description = "Animal unique id") @PathParam("id") String id) {
        return ucFindAnimal.findAnimal(id);
    }

    @GET
    @Path("name/{name}")
    public AnimalDto getAnimalByName(@PathParam("name") String name) {
        return ucFindAnimal.findAnimalByName(name);
    }

    @APIResponses({
    @APIResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = AnimalDto.class))),
    @APIResponse(responseCode = "404", description = "Animal not found"), @APIResponse(responseCode = "500")})
    @Operation(operationId = "deleteAnimalById", description = "Deletes the animal with given id")
    @DELETE
    @Path("{id}")
    public AnimalDto deleteAnimalById(@Parameter(description = "Animal unique id") @PathParam("id") String id) {
        return ucManageAnimal.deleteAnimal(id);
    }

    private static class PagedAnimalResponse extends PageResultDTO<AnimalDto> {
    }

}
