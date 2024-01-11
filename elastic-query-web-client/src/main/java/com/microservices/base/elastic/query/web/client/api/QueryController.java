package com.microservices.base.elastic.query.web.client.api;
//This class includes a single endpoint /query-by-text to query the elastic search documents by text
//as well as a couple of simple methods to be used with Thymeleaf.

import com.microservices.base.elastic.query.web.client.model.ElasticQueryWebClientRequestModel;
import com.microservices.base.elastic.query.web.client.model.ElasticQueryWebClientResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
//We cannot use @RestController here, because to be able to work with Thymeleaf, we shouldn't convert the response to JSON
//Remember RestController includes @ResponseBody which automatically converts response to a JSON, so RestController annotation cannot be used here as it needs a String and not JSON as response type.
public class QueryController {
  private static final Logger LOG = LoggerFactory.getLogger(QueryController.class);

  //Base path should go to the index page
    @GetMapping("") // No path is provided
    //It will open the index page when we call the webclient directly, without specifying any URI after the context path
    public String index(){
        return "index";
    }

    @GetMapping("/error") // In case of any error
    public String error(){
        return "error";
    }

    @GetMapping("/home")
    public String home(Model model){
        //Populates response model with empty response model for returning home string
        //this model is the key to interact with the thymeleaf templates
        //ui.Model class use to communicate between backend code and thymeleaf templates
        //Because any attribute we put on the model object can be reached on the thymeleaf templates by using thymeleaf expressions
        //If you open the home html, you will see a field definition with th:field="*{text}" id="text", this elasticQueryWebClientRequestModel in the controller will be read from the elasticQueryWebClientRequestModel in the thymeleaf template.
        //Remember we have a text field in the elasticQueryWebClientRequestModel object, so it will be read from the object in the thymeleaf template that we have set.
        //When user fill this input text field, it will put on the text property of the request model object and sent to the controller method.
        model.addAttribute("elasticQueryWebClientRequestModel",
                ElasticQueryWebClientRequestModel.builder().build());
        return "home";
    }

    @PostMapping("/query-by-text")
    //This method will be the action that will be called by the form, on the home template
    //@Valid -> to be validated by spring and text field will be checked if empty or not, thanks to the Not Empty annotation on the text field in the model
    public String queryByText(@Valid ElasticQueryWebClientRequestModel requestModel,
                              Model model){
        LOG.info("Querying with text {}", requestModel);
        List<ElasticQueryWebClientResponseModel> responseModels = new ArrayList<>();
        //dummy response model
        responseModels.add(ElasticQueryWebClientResponseModel.builder()
                .id("1")
                .text(requestModel.getText())
                .build());
        model.addAttribute("elasticQueryWebClientResponseModels", responseModels);
        model.addAttribute("searchText", requestModel.getText());
        //set the empty request model, to clear the text on the webpage
        model.addAttribute("elasticQueryWebClientRequestModel", ElasticQueryWebClientRequestModel.builder().build());
        return "home"; // return home string to render the home template, matching string of return type is always the name of the template to render and show in the response
    }

}
