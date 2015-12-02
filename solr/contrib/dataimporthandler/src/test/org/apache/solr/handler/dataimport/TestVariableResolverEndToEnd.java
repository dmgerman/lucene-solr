begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|TestVariableResolverEndToEnd
specifier|public
class|class
name|TestVariableResolverEndToEnd
extends|extends
name|AbstractDIHJdbcTestCase
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|h
operator|.
name|query
argument_list|(
literal|"/dataimport"
argument_list|,
name|generateRequest
argument_list|()
argument_list|)
expr_stmt|;
name|SolrQueryRequest
name|req
init|=
literal|null
decl_stmt|;
try|try
block|{
name|req
operator|=
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"wt"
argument_list|,
literal|"json"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|String
name|response
init|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|response
operator|=
name|response
operator|.
name|replaceAll
argument_list|(
literal|"\\s"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|response
operator|.
name|contains
argument_list|(
literal|"\"numFound\":1"
argument_list|)
argument_list|)
expr_stmt|;
name|Pattern
name|p
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[\"]second1_s[\"][:][\"](.*?)[\"]"
argument_list|)
decl_stmt|;
name|Matcher
name|m
init|=
name|p
operator|.
name|matcher
argument_list|(
name|response
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|m
operator|.
name|find
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|yearStr
init|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|response
operator|.
name|contains
argument_list|(
literal|"\"second1_s\":\""
operator|+
name|yearStr
operator|+
literal|"\""
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|response
operator|.
name|contains
argument_list|(
literal|"\"second2_s\":\""
operator|+
name|yearStr
operator|+
literal|"\""
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|response
operator|.
name|contains
argument_list|(
literal|"\"second3_s\":\""
operator|+
name|yearStr
operator|+
literal|"\""
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|response
operator|.
name|contains
argument_list|(
literal|"\"PORK_s\":\"GRILL\""
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|response
operator|.
name|contains
argument_list|(
literal|"\"FISH_s\":\"FRY\""
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|response
operator|.
name|contains
argument_list|(
literal|"\"BEEF_CUTS_mult_s\":[\"ROUND\",\"SIRLOIN\"]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|generateConfig
specifier|protected
name|String
name|generateConfig
parameter_list|()
block|{
name|String
name|thirdLocaleParam
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|""
else|:
operator|(
literal|", '"
operator|+
name|Locale
operator|.
name|getDefault
argument_list|()
operator|+
literal|"'"
operator|)
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<dataConfig> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<dataSource name=\"hsqldb\" driver=\"${dataimporter.request.dots.in.hsqldb.driver}\" url=\"jdbc:hsqldb:mem:.\" /> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<document name=\"TestEvaluators\"> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<entity name=\"FIRST\" processor=\"SqlEntityProcessor\" dataSource=\"hsqldb\" "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" query=\""
operator|+
literal|"select "
operator|+
literal|" 1 as id, "
operator|+
literal|" 'SELECT' as SELECT_KEYWORD, "
operator|+
literal|" CURRENT_TIMESTAMP as FIRST_TS "
operator|+
literal|"from DUAL \">\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<field column=\"SELECT_KEYWORD\" name=\"select_keyword_s\" /> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<entity name=\"SECOND\" processor=\"SqlEntityProcessor\" dataSource=\"hsqldb\" transformer=\"TemplateTransformer\" "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"   query=\""
operator|+
literal|"${dataimporter.functions.encodeUrl(FIRST.SELECT_KEYWORD)} "
operator|+
literal|" 1 as SORT, "
operator|+
literal|" CURRENT_TIMESTAMP as SECOND_TS, "
operator|+
literal|" '${dataimporter.functions.formatDate(FIRST.FIRST_TS, 'yyyy'"
operator|+
name|thirdLocaleParam
operator|+
literal|")}' as SECOND1_S,  "
operator|+
literal|" 'PORK' AS MEAT, "
operator|+
literal|" 'GRILL' AS METHOD, "
operator|+
literal|" 'ROUND' AS CUTS, "
operator|+
literal|" 'BEEF_CUTS' AS WHATKIND "
operator|+
literal|"from DUAL "
operator|+
literal|"WHERE 1=${FIRST.ID} "
operator|+
literal|"UNION "
operator|+
literal|"${dataimporter.functions.encodeUrl(FIRST.SELECT_KEYWORD)} "
operator|+
literal|" 2 as SORT, "
operator|+
literal|" CURRENT_TIMESTAMP as SECOND_TS, "
operator|+
literal|" '${dataimporter.functions.formatDate(FIRST.FIRST_TS, 'yyyy'"
operator|+
name|thirdLocaleParam
operator|+
literal|")}' as SECOND1_S,  "
operator|+
literal|" 'FISH' AS MEAT, "
operator|+
literal|" 'FRY' AS METHOD, "
operator|+
literal|" 'SIRLOIN' AS CUTS, "
operator|+
literal|" 'BEEF_CUTS' AS WHATKIND "
operator|+
literal|"from DUAL "
operator|+
literal|"WHERE 1=${FIRST.ID} "
operator|+
literal|"ORDER BY SORT \""
operator|+
literal|">\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<field column=\"SECOND_S\" name=\"second_s\" /> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<field column=\"SECOND1_S\" name=\"second1_s\" /> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<field column=\"second2_s\" template=\"${dataimporter.functions.formatDate(SECOND.SECOND_TS, 'yyyy'"
operator|+
name|thirdLocaleParam
operator|+
literal|")}\" /> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<field column=\"second3_s\" template=\"${dih.functions.formatDate(SECOND.SECOND_TS, 'yyyy'"
operator|+
name|thirdLocaleParam
operator|+
literal|")}\" /> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<field column=\"METHOD\" name=\"${SECOND.MEAT}_s\"/>\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<field column=\"CUTS\" name=\"${SECOND.WHATKIND}_mult_s\"/>\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</entity>\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</entity>\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</document> \n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</dataConfig> \n"
argument_list|)
expr_stmt|;
name|String
name|config
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|config
argument_list|)
expr_stmt|;
return|return
name|config
return|;
block|}
annotation|@
name|Override
DECL|method|populateData
specifier|protected
name|void
name|populateData
parameter_list|(
name|Connection
name|conn
parameter_list|)
throws|throws
name|Exception
block|{
name|Statement
name|s
init|=
literal|null
decl_stmt|;
try|try
block|{
name|s
operator|=
name|conn
operator|.
name|createStatement
argument_list|()
expr_stmt|;
name|s
operator|.
name|executeUpdate
argument_list|(
literal|"create table dual(dual char(1) not null)"
argument_list|)
expr_stmt|;
name|s
operator|.
name|executeUpdate
argument_list|(
literal|"insert into dual values('Y')"
argument_list|)
expr_stmt|;
name|conn
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
try|try
block|{
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
try|try
block|{
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
block|}
block|}
annotation|@
name|Override
DECL|method|setAllowedDatabases
specifier|protected
name|Database
name|setAllowedDatabases
parameter_list|()
block|{
return|return
name|Database
operator|.
name|HSQLDB
return|;
block|}
block|}
end_class

end_unit

