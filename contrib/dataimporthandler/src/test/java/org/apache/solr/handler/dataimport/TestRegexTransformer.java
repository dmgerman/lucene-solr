begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|RegexTransformer
operator|.
name|REGEX
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|RegexTransformer
operator|.
name|GROUP_NAMES
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|DataImporter
operator|.
name|COLUMN
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  *<p> Test for RegexTransformer</p>  *  * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|TestRegexTransformer
specifier|public
class|class
name|TestRegexTransformer
block|{
annotation|@
name|Test
DECL|method|commaSeparated
specifier|public
name|void
name|commaSeparated
parameter_list|()
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|//<field column="col1" sourceColName="a" splitBy="," />
name|fields
operator|.
name|add
argument_list|(
name|getField
argument_list|(
literal|"col1"
argument_list|,
literal|"string"
argument_list|,
literal|null
argument_list|,
literal|"a"
argument_list|,
literal|","
argument_list|)
argument_list|)
expr_stmt|;
name|Context
name|context
init|=
name|AbstractDataImportHandlerTest
operator|.
name|getContext
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
name|fields
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|src
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|src
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|"a,bb,cc,d"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|result
init|=
operator|new
name|RegexTransformer
argument_list|()
operator|.
name|transformRow
argument_list|(
name|src
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4
argument_list|,
operator|(
operator|(
name|List
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"col1"
argument_list|)
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|groupNames
specifier|public
name|void
name|groupNames
parameter_list|()
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|//<field column="col1" regex="(\w*)(\w*) (\w*)" groupNames=",firstName,lastName"/>
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|m
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
name|COLUMN
argument_list|,
literal|"fullName"
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
name|GROUP_NAMES
argument_list|,
literal|",firstName,lastName"
argument_list|)
expr_stmt|;
name|m
operator|.
name|put
argument_list|(
name|REGEX
argument_list|,
literal|"(\\w*) (\\w*) (\\w*)"
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|Context
name|context
init|=
name|AbstractDataImportHandlerTest
operator|.
name|getContext
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
name|fields
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|src
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|src
operator|.
name|put
argument_list|(
literal|"fullName"
argument_list|,
literal|"Mr Noble Paul"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|result
init|=
operator|new
name|RegexTransformer
argument_list|()
operator|.
name|transformRow
argument_list|(
name|src
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Noble"
argument_list|,
name|result
operator|.
name|get
argument_list|(
literal|"firstName"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Paul"
argument_list|,
name|result
operator|.
name|get
argument_list|(
literal|"lastName"
argument_list|)
argument_list|)
expr_stmt|;
name|src
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
literal|"Mr Noble Paul"
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
literal|"Mr Shalin Mangar"
argument_list|)
expr_stmt|;
name|src
operator|.
name|put
argument_list|(
literal|"fullName"
argument_list|,
name|l
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|RegexTransformer
argument_list|()
operator|.
name|transformRow
argument_list|(
name|src
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|List
name|l1
init|=
operator|(
name|List
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"firstName"
argument_list|)
decl_stmt|;
name|List
name|l2
init|=
operator|(
name|List
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"lastName"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Noble"
argument_list|,
name|l1
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Shalin"
argument_list|,
name|l1
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Paul"
argument_list|,
name|l2
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Mangar"
argument_list|,
name|l2
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|replaceWith
specifier|public
name|void
name|replaceWith
parameter_list|()
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|//<field column="name" sourceColName="a" regexp="'" replaceWith="''" />
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|fld
init|=
name|getField
argument_list|(
literal|"name"
argument_list|,
literal|"string"
argument_list|,
literal|"'"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|fld
operator|.
name|put
argument_list|(
literal|"replaceWith"
argument_list|,
literal|"''"
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|fld
argument_list|)
expr_stmt|;
name|Context
name|context
init|=
name|AbstractDataImportHandlerTest
operator|.
name|getContext
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
name|fields
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|src
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|s
init|=
literal|"D'souza"
decl_stmt|;
name|src
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|result
init|=
operator|new
name|RegexTransformer
argument_list|()
operator|.
name|transformRow
argument_list|(
name|src
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"D''souza"
argument_list|,
name|result
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|mileage
specifier|public
name|void
name|mileage
parameter_list|()
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|fields
init|=
name|getFields
argument_list|()
decl_stmt|;
comment|// add another regex which reuses result from previous regex again!
comment|//<field column="hltCityMPG" sourceColName="rowdata" regexp="(${e.city_mileage})" />
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|fld
init|=
name|getField
argument_list|(
literal|"hltCityMPG"
argument_list|,
literal|"string"
argument_list|,
literal|".*(${e.city_mileage})"
argument_list|,
literal|"rowdata"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|fld
operator|.
name|put
argument_list|(
literal|"replaceWith"
argument_list|,
literal|"*** $1 ***"
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|fld
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|row
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|s
init|=
literal|"Fuel Economy Range: 26 mpg Hwy, 19 mpg City"
decl_stmt|;
name|row
operator|.
name|put
argument_list|(
literal|"rowdata"
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|VariableResolverImpl
name|resolver
init|=
operator|new
name|VariableResolverImpl
argument_list|()
decl_stmt|;
name|resolver
operator|.
name|addNamespace
argument_list|(
literal|"e"
argument_list|,
name|row
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|eAttrs
init|=
name|AbstractDataImportHandlerTest
operator|.
name|createMap
argument_list|(
literal|"name"
argument_list|,
literal|"e"
argument_list|)
decl_stmt|;
name|Context
name|context
init|=
name|AbstractDataImportHandlerTest
operator|.
name|getContext
argument_list|(
literal|null
argument_list|,
name|resolver
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
name|fields
argument_list|,
name|eAttrs
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|result
init|=
operator|new
name|RegexTransformer
argument_list|()
operator|.
name|transformRow
argument_list|(
name|row
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|s
argument_list|,
name|result
operator|.
name|get
argument_list|(
literal|"rowdata"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"26"
argument_list|,
name|result
operator|.
name|get
argument_list|(
literal|"highway_mileage"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"19"
argument_list|,
name|result
operator|.
name|get
argument_list|(
literal|"city_mileage"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"*** 19 *** mpg City"
argument_list|,
name|result
operator|.
name|get
argument_list|(
literal|"hltCityMPG"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getFields
specifier|public
specifier|static
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|getFields
parameter_list|()
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|//<field column="city_mileage" sourceColName="rowdata" regexp=
comment|//    "Fuel Economy Range:\\s*?\\d*?\\s*?mpg Hwy,\\s*?(\\d*?)\\s*?mpg City"
name|fields
operator|.
name|add
argument_list|(
name|getField
argument_list|(
literal|"city_mileage"
argument_list|,
literal|"sint"
argument_list|,
literal|"Fuel Economy Range:\\s*?\\d*?\\s*?mpg Hwy,\\s*?(\\d*?)\\s*?mpg City"
argument_list|,
literal|"rowdata"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|//<field column="highway_mileage" sourceColName="rowdata" regexp=
comment|//    "Fuel Economy Range:\\s*?(\\d*?)\\s*?mpg Hwy,\\s*?\\d*?\\s*?mpg City"
name|fields
operator|.
name|add
argument_list|(
name|getField
argument_list|(
literal|"highway_mileage"
argument_list|,
literal|"sint"
argument_list|,
literal|"Fuel Economy Range:\\s*?(\\d*?)\\s*?mpg Hwy,\\s*?\\d*?\\s*?mpg City"
argument_list|,
literal|"rowdata"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|//<field column="seating_capacity" sourceColName="rowdata" regexp="Seating capacity:(.*)"
name|fields
operator|.
name|add
argument_list|(
name|getField
argument_list|(
literal|"seating_capacity"
argument_list|,
literal|"sint"
argument_list|,
literal|"Seating capacity:(.*)"
argument_list|,
literal|"rowdata"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|//<field column="warranty" sourceColName="rowdata" regexp="Warranty:(.*)" />
name|fields
operator|.
name|add
argument_list|(
name|getField
argument_list|(
literal|"warranty"
argument_list|,
literal|"string"
argument_list|,
literal|"Warranty:(.*)"
argument_list|,
literal|"rowdata"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|//<field column="rowdata" sourceColName="rowdata" />
name|fields
operator|.
name|add
argument_list|(
name|getField
argument_list|(
literal|"rowdata"
argument_list|,
literal|"string"
argument_list|,
literal|null
argument_list|,
literal|"rowdata"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|fields
return|;
block|}
DECL|method|getField
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getField
parameter_list|(
name|String
name|col
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|re
parameter_list|,
name|String
name|srcCol
parameter_list|,
name|String
name|splitBy
parameter_list|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|vals
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|vals
operator|.
name|put
argument_list|(
literal|"column"
argument_list|,
name|col
argument_list|)
expr_stmt|;
name|vals
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|vals
operator|.
name|put
argument_list|(
literal|"regex"
argument_list|,
name|re
argument_list|)
expr_stmt|;
name|vals
operator|.
name|put
argument_list|(
literal|"sourceColName"
argument_list|,
name|srcCol
argument_list|)
expr_stmt|;
name|vals
operator|.
name|put
argument_list|(
literal|"splitBy"
argument_list|,
name|splitBy
argument_list|)
expr_stmt|;
return|return
name|vals
return|;
block|}
block|}
end_class

end_unit

