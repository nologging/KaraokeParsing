package org.karaoke.graphql;

import graphql.GraphQL;
import graphql.TypeResolutionEnvironment;
import graphql.schema.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLEnumType.newEnum;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

@Component
@Slf4j
public class GraphQLBuilder {

    @Autowired
    DataFetcher dataFetcher;

    private GraphQL graphQL;

    // 이 코드는 조금 수정해야겟다.
    @PostConstruct
    public void setUp(){

        GraphQLObjectType Karaoke = newObject()
                .name("Karaoke")
                .description("노래방 번호,제목,가수명을 갖고있는 Type 입니다. ")
                .field(newFieldDefinition()
                        .name("number")
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name("title")
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name("singer")
                        .type(GraphQLString))
                .build();

         GraphQLEnumType company = newEnum()
                .name("COMPANY")
                .value("KY")
                .value("TJ")
                .build();

         GraphQLEnumType category = newEnum()
                .name("CATEGORY")
                .value("SINGER")
                .value("SONG")
                .build();

        GraphQLInputObjectType karaoke = GraphQLInputObjectType.newInputObject()
                .name("karaoke")
                .field(GraphQLInputObjectField.newInputObjectField()
                        .name("company")
                        .type(company)
                        .build())
                .field(GraphQLInputObjectField.newInputObjectField()
                        .name("category")
                        .type(category)
                        .build())
                .field(GraphQLInputObjectField.newInputObjectField()
                        .name("keyword")
                        .type(GraphQLString)
                        .build())
                .build();





        // 여기부턴 학습용

        GraphQLObjectType inter1 = newObject()
                .name("Karaoke1")
                .description("노래방 번호,제목,가수명을 갖고있는 Type 입니다. ")
                .field(newFieldDefinition()
                        .name("name")
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name("title")
                        .type(GraphQLString))
                .build();
        GraphQLObjectType inter2 = newObject()
                .name("Karaoke2")
                .description("노래방 번호,제목,가수명을 갖고있는 Type 입니다. ")
                .field(newFieldDefinition()
                        .name("name")
                        .type(GraphQLString))
                .field(newFieldDefinition()
                        .name("tt")
                        .type(GraphQLString))
                .build();

        GraphQLUnionType PetType = GraphQLUnionType.newUnionType()
                .name("Hello")
                .possibleType(inter1)
                .possibleType(inter2)
                .typeResolver(
                        new TypeResolver() {
                            @Override
                            public GraphQLObjectType getType(TypeResolutionEnvironment env) {
                                log.info("{}", env.getObject().toString());
                                if(env.getObject() instanceof  Map){
                                    return inter2;
                                }else{
                                    return inter1;
                                }

                            }
                        }
                )
                .build();

        GraphQLObjectType objectType = newObject().name("selectKaraoke")
                .field(newFieldDefinition()
                        .name("Karaoke")
                        .type(new GraphQLList(Karaoke))
                        .dataFetcher(dataFetcher)
                        .argument(GraphQLArgument.newArgument()
                                .name("karaoke")
                                .type(karaoke))
                        .argument(GraphQLArgument.newArgument()
                                .name("page")
                                .type(GraphQLInt)))
                .field(newFieldDefinition()
                        .name("Karaoke11")
                        .type(PetType)
                        .dataFetcher((env)-> {
                            Map<String ,String> map = new HashMap<>();
                            map.put("title","제목");
                            map.put("tt","제목1");
                            return map;
                        } ))
                .build();


        GraphQLSchema schema = GraphQLSchema.newSchema()
                .query(objectType)
                .build();

        graphQL = GraphQL.newGraphQL(schema)
                .build();
    }

    @Bean
    public GraphQL getGraphQL() {
        return graphQL;
    }
}
