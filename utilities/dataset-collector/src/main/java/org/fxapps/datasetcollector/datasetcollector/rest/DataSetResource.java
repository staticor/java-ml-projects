package org.fxapps.datasetcollector.datasetcollector.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.fxapps.datasetcollector.datasetcollector.service.DatasetService;
import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;

@Path("dataset")
public class DataSetResource {

	@Inject
	DatasetService service;

	@Context
	private HttpServletRequest servletRequest;

	@Inject
	Logger logger;

	@POST
	@Path("{label}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void receiveFile(MultipartInput input, @PathParam("label") String label) {
		List<InputPart> parts = input.getParts();
		parts.stream().filter(p -> p.getMediaType().getType().startsWith("image"))
				.forEach(p -> this.saveImage(p, label));
		input.close();
	}

	@GET
	@Path("labels")
	@Produces("application/json")
	public List<String> listLabels() {
		return service.listLabels();
	}

	private void saveImage(InputPart part, String label) {
		try {
			logger.info("Receiving image from " + servletRequest.getRemoteAddr());
			InputStream body = part.getBody(new GenericType<InputStream>(InputStream.class));
			service.store(body, part.getMediaType().getSubtype(), label);
			logger.info("Image from " + servletRequest.getRemoteAddr() + " received and stored with sucess!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
