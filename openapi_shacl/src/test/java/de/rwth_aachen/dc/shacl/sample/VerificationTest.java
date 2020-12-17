package de.rwth_aachen.dc.shacl.sample;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.net.URL;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.util.ModelPrinter;
import org.topbraid.shacl.validation.ValidationUtil;

public class VerificationTest {

	public VerificationTest()
	{
		 URL rdffile_url = ClassLoader.getSystemResource("Duplex_A_20110907_LBD.ttl");
		 URL shaclfile_url = ClassLoader.getSystemResource("SHACL_rulesetLevel2.ttl");
	        try {
	            File rdfFile = new File(rdffile_url.toURI());
	            File shaclFile = new File(shaclfile_url.toURI());
	            
	            
	    		Model dataModel = JenaUtil.createMemoryModel();
	    		Model sahapesModel = JenaUtil.createMemoryModel();
	    		RDFDataMgr.read(dataModel, rdfFile.getAbsolutePath()); 
	    		RDFDataMgr.read(sahapesModel, shaclFile.getAbsolutePath()); 

	    		Resource report = ValidationUtil.validateModel(dataModel, sahapesModel, true);
	    		// Print violations
	    		System.out.println(ModelPrinter.get().print(report.getModel()));// Print violations
	    		System.out.println(ModelPrinter.get().print(report.getModel()));
	        } catch (Exception e) {
	        	e.printStackTrace();
	            fail("Conversion had an error: " + e.getMessage());
	        }
	}
	
	public static void main(String[] args) {
		new VerificationTest();
	}
}
