package org.karaoke.restcontroller;


import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import lombok.extern.slf4j.Slf4j;
import org.karaoke.domain.Argument;
import org.karaoke.domain.GraphQLInput;
import org.karaoke.domain.GraphQLQuery;
import org.karaoke.graphql.PersistentQueryMap;
import org.karaoke.service.KaraokeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@CrossOrigin
@RestController
@Slf4j
public class KaraokeController {

    KaraokeService parser;

    GraphQL graphQL;

    @Autowired
    PersistentQueryMap persistentQueryMap;

    @Autowired
    public void setParser(KaraokeService parser) {
        this.parser = parser;
    }

    @Autowired
    public void setGraphQL(GraphQL graphQL) {
        this.graphQL = graphQL;
    }

    //Rest Controller
    @GetMapping("/{company}/{category}/{word}")
    public List<?> selectKaraoke(@ModelAttribute Argument argument, @RequestParam(required = false, defaultValue = "1") int page) throws IOException {
        return parser.parseKaraoke(argument, page).getKaraokes();
    }

    //GraphQL Controller
    @PostMapping("/karaokeGraphiQL")
    public CompletableFuture<ExecutionResult> selectByGraphiQL(@RequestBody GraphQLInput input) {

        return graphQL.executeAsync(buildExecutionInput(input));
    }

    @PostMapping("/karaokeGraphQL")
    public CompletableFuture<ExecutionResult> selectByGraphQL(@RequestBody GraphQLInput input) {
        return graphQL.executeAsync(buildExecutionInput(input));
    }

    private ExecutionInput buildExecutionInput(GraphQLInput input) {
        return ExecutionInput.newExecutionInput()
                .query(input.getQuery())
                .variables(input.getVariable())
                .build();
    }


}
