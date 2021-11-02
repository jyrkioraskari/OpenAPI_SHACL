package de.rwth_aachen.dc.shacl.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.rules.RuleUtil;
import org.topbraid.shacl.validation.ValidationUtil;

/*
 * Jyrki Oraskari, 2021
 */

@Path("/")
public class OpenAPI_SHACL {


	
	/**
	 * @param accept_type
	 * @param rdfFile RDF file of the model in the TTL format
	 * @param shaclFile SHACL rules in the TTL format
	 * @return
	 */
	@POST
	@Path("/filter")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ "text/turtle", "application/ld+json", "application/rdf+xml" })
	public Response filter(@HeaderParam(HttpHeaders.ACCEPT) String accept_type,
			@FormDataParam("rdfFile") InputStream rdfFile,@FormDataParam("shaclFile") InputStream shaclFile) {
		try {
			File tempRdfFile = File.createTempFile("rdf-", ".ttl");
			tempRdfFile.deleteOnExit();

			File tempSHACLFile = File.createTempFile("shacl-", ".ttl");
			tempSHACLFile.deleteOnExit();
			System.out.println("rdfFile:"+rdfFile);
			System.out.println("tempRdfFile:"+tempRdfFile);

			Files.copy(rdfFile, tempRdfFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			rdfFile.close();
			Files.copy(shaclFile, tempSHACLFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			shaclFile.close();

			return handle_filter(accept_type, tempRdfFile,tempSHACLFile);

		} catch (Exception e) {
			e.printStackTrace();
			
		}

		return Response.noContent().build();
	}
	
	
	/**
	 * @param accept_type
	 * @param rdfFile RDF file of the model in the TTL format
	 * @param shaclFile SHACL rules in the TTL format
	 * @return
	 */
	@POST
	@Path("/check")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ "text/turtle", "application/ld+json", "application/rdf+xml" })
	public Response check(@HeaderParam(HttpHeaders.ACCEPT) String accept_type,
			@FormDataParam("rdfFile") InputStream rdfFile,@FormDataParam("shaclFile") InputStream shaclFile) {
		try {
			File tempRdfFile = File.createTempFile("rdf-", ".ttl");
			tempRdfFile.deleteOnExit();

			File tempSHACLFile = File.createTempFile("shacl-", ".ttl");
			tempSHACLFile.deleteOnExit();
			System.out.println("rdfFile:"+rdfFile);
			System.out.println("tempRdfFile:"+tempRdfFile);

			Files.copy(rdfFile, tempRdfFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			rdfFile.close();
			Files.copy(shaclFile, tempSHACLFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			shaclFile.close();

			return handle_check(accept_type, tempRdfFile,tempSHACLFile);

		} catch (Exception e) {
			e.printStackTrace();
			
		}

		return Response.noContent().build();
	}
	

	private Response handle_check(String accept_type, File tempIfcFile,File tempSHACLFile) {
		if (accept_type.equals("application/ld+json")) {
			StringBuilder result_string = new StringBuilder();
			execute_check(tempIfcFile, tempSHACLFile, result_string, RDFFormat.JSONLD_COMPACT_PRETTY);
			System.out.println(result_string.toString());
			return Response.ok(result_string.toString(), "application/ld+json").build();
		} else if (accept_type.equals("application/rdf+xml")) {
			StringBuilder result_string = new StringBuilder();
			execute_check(tempIfcFile, tempSHACLFile, result_string, RDFFormat.RDFXML);
			System.out.println(result_string.toString());
			return Response.ok(result_string.toString(), "application/rdf+xml").build();
		} else {
			StringBuilder result_string = new StringBuilder();
			execute_check(tempIfcFile, tempSHACLFile, result_string, RDFFormat.TURTLE_PRETTY);
			System.out.println(result_string.toString());
			return Response.ok(result_string.toString(), "text/turtle").build();

		}
	}

	private void execute_check(File rdfFile,File shaclFile, StringBuilder result_string, RDFFormat rdfformat) {

		Model dataModel = JenaUtil.createMemoryModel();
		Model shapesModel = JenaUtil.createMemoryModel();
		RDFDataMgr.read(dataModel, rdfFile.getAbsolutePath()); 
		RDFDataMgr.read(shapesModel, shaclFile.getAbsolutePath()); 

		Resource report = ValidationUtil.validateModel(dataModel, shapesModel, true);

		OutputStream ttl_output = new OutputStream() {
			private StringBuilder string = new StringBuilder();

			@Override
			public void write(int b) throws IOException {
				this.string.append((char) b);
			}

			public String toString() {
				return this.string.toString();
			}
		};
		RDFDataMgr.write(ttl_output, report.getModel(), rdfformat);
		result_string.append(ttl_output.toString());
	}

	private Response handle_filter(String accept_type, File tempIfcFile,File tempSHACLFile) {
		if (accept_type.equals("application/ld+json")) {
			StringBuilder result_string = new StringBuilder();
			execute_filter(tempIfcFile, tempSHACLFile, result_string, RDFFormat.JSONLD_COMPACT_PRETTY);
			System.out.println(result_string.toString());
			return Response.ok(result_string.toString(), "application/ld+json").build();
		} else if (accept_type.equals("application/rdf+xml")) {
			StringBuilder result_string = new StringBuilder();
			execute_filter(tempIfcFile, tempSHACLFile, result_string, RDFFormat.RDFXML);
			System.out.println(result_string.toString());
			return Response.ok(result_string.toString(), "application/rdf+xml").build();
		} else {
			StringBuilder result_string = new StringBuilder();
			execute_filter(tempIfcFile, tempSHACLFile, result_string, RDFFormat.TURTLE_PRETTY);
			System.out.println(result_string.toString());
			return Response.ok(result_string.toString(), "text/turtle").build();

		}
	}

	private void execute_filter(File rdfFile,File shaclFile, StringBuilder result_string, RDFFormat rdfformat) {

		Model dataModel = JenaUtil.createMemoryModel();
		Model shapesModel = JenaUtil.createMemoryModel();
		RDFDataMgr.read(dataModel, rdfFile.getAbsolutePath()); 
		RDFDataMgr.read(shapesModel, shaclFile.getAbsolutePath()); 

        Model inferenceModel = JenaUtil.createDefaultModel(); 
        inferenceModel = RuleUtil.executeRules(dataModel, shapesModel, inferenceModel, null);  
        
		
		
		OutputStream ttl_output = new OutputStream() {
			private StringBuilder string = new StringBuilder();

			@Override
			public void write(int b) throws IOException {
				this.string.append((char) b);
			}

			public String toString() {
				return this.string.toString();
			}
		};
		RDFDataMgr.write(ttl_output, inferenceModel, rdfformat);
		result_string.append(ttl_output.toString());
	}

}