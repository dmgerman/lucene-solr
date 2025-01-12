begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|join
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|HashSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
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
name|client
operator|.
name|solrj
operator|.
name|request
operator|.
name|CollectionAdminRequest
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|FacetField
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|FacetField
operator|.
name|Count
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|QueryResponse
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
name|cloud
operator|.
name|SolrCloudTestCase
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
name|common
operator|.
name|SolrInputDocument
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
name|common
operator|.
name|params
operator|.
name|ModifiableSolrParams
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
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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

begin_class
DECL|class|BlockJoinFacetDistribTest
specifier|public
class|class
name|BlockJoinFacetDistribTest
extends|extends
name|SolrCloudTestCase
block|{
DECL|field|collection
specifier|private
specifier|static
specifier|final
name|String
name|collection
init|=
literal|"facetcollection"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupCluster
specifier|public
specifier|static
name|void
name|setupCluster
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|configDir
init|=
name|Paths
operator|.
name|get
argument_list|(
name|TEST_HOME
argument_list|()
argument_list|,
literal|"collection1"
argument_list|,
literal|"conf"
argument_list|)
decl_stmt|;
name|String
name|configName
init|=
literal|"solrCloudCollectionConfig"
decl_stmt|;
name|int
name|nodeCount
init|=
literal|6
decl_stmt|;
name|configureCluster
argument_list|(
name|nodeCount
argument_list|)
operator|.
name|addConfig
argument_list|(
name|configName
argument_list|,
name|configDir
argument_list|)
operator|.
name|configure
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|collectionProperties
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|collectionProperties
operator|.
name|put
argument_list|(
literal|"config"
argument_list|,
literal|"solrconfig-blockjoinfacetcomponent.xml"
argument_list|)
expr_stmt|;
name|collectionProperties
operator|.
name|put
argument_list|(
literal|"schema"
argument_list|,
literal|"schema-blockjoinfacetcomponent.xml"
argument_list|)
expr_stmt|;
comment|// create a collection holding data for the "to" side of the JOIN
name|int
name|shards
init|=
literal|3
decl_stmt|;
name|int
name|replicas
init|=
literal|2
decl_stmt|;
name|CollectionAdminRequest
operator|.
name|createCollection
argument_list|(
name|collection
argument_list|,
name|configName
argument_list|,
name|shards
argument_list|,
name|replicas
argument_list|)
operator|.
name|setProperties
argument_list|(
name|collectionProperties
argument_list|)
operator|.
name|process
argument_list|(
name|cluster
operator|.
name|getSolrClient
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|field|colors
specifier|final
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|colors
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"red"
argument_list|,
literal|"blue"
argument_list|,
literal|"brown"
argument_list|,
literal|"white"
argument_list|,
literal|"black"
argument_list|,
literal|"yellow"
argument_list|,
literal|"cyan"
argument_list|,
literal|"magenta"
argument_list|,
literal|"blur"
argument_list|,
literal|"fuchsia"
argument_list|,
literal|"light"
argument_list|,
literal|"dark"
argument_list|,
literal|"green"
argument_list|,
literal|"grey"
argument_list|,
literal|"don't"
argument_list|,
literal|"know"
argument_list|,
literal|"any"
argument_list|,
literal|"more"
argument_list|)
decl_stmt|;
DECL|field|sizes
specifier|final
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|sizes
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"s"
argument_list|,
literal|"m"
argument_list|,
literal|"l"
argument_list|,
literal|"xl"
argument_list|,
literal|"xxl"
argument_list|,
literal|"xml"
argument_list|,
literal|"xxxl"
argument_list|,
literal|"3"
argument_list|,
literal|"4"
argument_list|,
literal|"5"
argument_list|,
literal|"6"
argument_list|,
literal|"petite"
argument_list|,
literal|"maxi"
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testBJQFacetComponent
specifier|public
name|void
name|testBJQFacetComponent
parameter_list|()
throws|throws
name|Exception
block|{
assert|assert
operator|!
name|colors
operator|.
name|removeAll
argument_list|(
name|sizes
argument_list|)
operator|:
literal|"there is no colors in sizes"
assert|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|colors
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|matchingColors
init|=
name|colors
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|atLeast
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|)
argument_list|,
name|colors
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|parentIdsByAttrValue
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Integer
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Integer
argument_list|>
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|super
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|==
literal|null
operator|&&
name|put
argument_list|(
operator|(
name|String
operator|)
name|key
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|)
operator|==
literal|null
condition|?
name|super
operator|.
name|get
argument_list|(
name|key
argument_list|)
else|:
name|super
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|deleteByQuery
argument_list|(
name|collection
argument_list|,
literal|"*:*"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|parents
init|=
name|atLeast
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|boolean
name|aggregationOccurs
init|=
literal|false
decl_stmt|;
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|parentDocs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|parent
init|=
literal|0
init|;
name|parent
operator|<
name|parents
operator|||
operator|!
name|aggregationOccurs
condition|;
name|parent
operator|++
control|)
block|{
assert|assert
name|parent
operator|<
literal|2000000
operator|:
literal|"parent num "
operator|+
name|parent
operator|+
literal|" aggregationOccurs:"
operator|+
name|aggregationOccurs
operator|+
literal|". Sorry! too tricky loop condition."
assert|;
name|SolrInputDocument
name|pdoc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|pdoc
operator|.
name|addField
argument_list|(
literal|"id"
argument_list|,
name|parent
argument_list|)
expr_stmt|;
name|pdoc
operator|.
name|addField
argument_list|(
literal|"type_s"
argument_list|,
literal|"parent"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|parentBrand
init|=
literal|"brand"
operator|+
operator|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|)
decl_stmt|;
name|pdoc
operator|.
name|addField
argument_list|(
literal|"BRAND_s"
argument_list|,
name|parentBrand
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|child
init|=
literal|0
init|;
name|child
operator|<
name|atLeast
argument_list|(
name|colors
operator|.
name|size
argument_list|()
operator|/
literal|2
argument_list|)
condition|;
name|child
operator|++
control|)
block|{
name|SolrInputDocument
name|childDoc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
specifier|final
name|String
name|color
init|=
name|colors
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|colors
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|childDoc
operator|.
name|addField
argument_list|(
literal|"COLOR_s"
argument_list|,
name|color
argument_list|)
expr_stmt|;
specifier|final
name|String
name|size
init|=
name|sizes
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|sizes
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|childDoc
operator|.
name|addField
argument_list|(
literal|"SIZE_s"
argument_list|,
name|size
argument_list|)
expr_stmt|;
if|if
condition|(
name|matchingColors
operator|.
name|contains
argument_list|(
name|color
argument_list|)
condition|)
block|{
specifier|final
name|boolean
name|colorDupe
init|=
operator|!
name|parentIdsByAttrValue
operator|.
name|get
argument_list|(
name|color
argument_list|)
operator|.
name|add
argument_list|(
name|parent
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|sizeDupe
init|=
operator|!
name|parentIdsByAttrValue
operator|.
name|get
argument_list|(
name|size
argument_list|)
operator|.
name|add
argument_list|(
name|parent
argument_list|)
decl_stmt|;
name|aggregationOccurs
operator||=
name|colorDupe
operator|||
name|sizeDupe
expr_stmt|;
block|}
name|pdoc
operator|.
name|addChildDocument
argument_list|(
name|childDoc
argument_list|)
expr_stmt|;
block|}
name|parentDocs
operator|.
name|add
argument_list|(
name|pdoc
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|parentDocs
operator|.
name|isEmpty
argument_list|()
operator|&&
name|rarely
argument_list|()
condition|)
block|{
name|indexDocs
argument_list|(
name|parentDocs
argument_list|)
expr_stmt|;
name|parentDocs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|commit
argument_list|(
name|collection
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|parentDocs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|indexDocs
argument_list|(
name|parentDocs
argument_list|)
expr_stmt|;
block|}
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|commit
argument_list|(
name|collection
argument_list|)
expr_stmt|;
comment|// to parent query
specifier|final
name|String
name|childQueryClause
init|=
literal|"COLOR_s:("
operator|+
operator|(
name|matchingColors
operator|.
name|toString
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"[,\\[\\]]"
argument_list|,
literal|" "
argument_list|)
operator|)
operator|+
literal|")"
decl_stmt|;
specifier|final
name|boolean
name|oldFacetsEnabled
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|QueryResponse
name|results
init|=
name|query
argument_list|(
literal|"q"
argument_list|,
literal|"{!parent which=\"type_s:parent\"}"
operator|+
name|childQueryClause
argument_list|,
literal|"facet"
argument_list|,
name|oldFacetsEnabled
condition|?
literal|"true"
else|:
literal|"false"
argument_list|,
comment|// try to enforce multiple phases
name|oldFacetsEnabled
condition|?
literal|"facet.field"
else|:
literal|"ignore"
argument_list|,
literal|"BRAND_s"
argument_list|,
name|oldFacetsEnabled
operator|&&
name|usually
argument_list|()
condition|?
literal|"facet.limit"
else|:
literal|"ignore"
argument_list|,
literal|"1"
argument_list|,
name|oldFacetsEnabled
operator|&&
name|usually
argument_list|()
condition|?
literal|"facet.mincount"
else|:
literal|"ignore"
argument_list|,
literal|"2"
argument_list|,
name|oldFacetsEnabled
operator|&&
name|usually
argument_list|()
condition|?
literal|"facet.overrequest.count"
else|:
literal|"ignore"
argument_list|,
literal|"0"
argument_list|,
literal|"qt"
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"blockJoinDocSetFacetRH"
else|:
literal|"blockJoinFacetRH"
argument_list|,
literal|"child.facet.field"
argument_list|,
literal|"COLOR_s"
argument_list|,
literal|"child.facet.field"
argument_list|,
literal|"SIZE_s"
argument_list|,
literal|"distrib.singlePass"
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"true"
else|:
literal|"false"
argument_list|,
literal|"rows"
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"0"
else|:
literal|"10"
argument_list|)
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|resultsResponse
init|=
name|results
operator|.
name|getResponse
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|resultsResponse
argument_list|)
expr_stmt|;
name|FacetField
name|color_s
init|=
name|results
operator|.
name|getFacetField
argument_list|(
literal|"COLOR_s"
argument_list|)
decl_stmt|;
name|FacetField
name|size_s
init|=
name|results
operator|.
name|getFacetField
argument_list|(
literal|"SIZE_s"
argument_list|)
decl_stmt|;
name|String
name|msg
init|=
literal|""
operator|+
name|parentIdsByAttrValue
operator|+
literal|" "
operator|+
name|color_s
operator|+
literal|" "
operator|+
name|size_s
decl_stmt|;
for|for
control|(
name|FacetField
name|facet
range|:
operator|new
name|FacetField
index|[]
block|{
name|color_s
block|,
name|size_s
block|}
control|)
block|{
for|for
control|(
name|Count
name|c
range|:
name|facet
operator|.
name|getValues
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|c
operator|.
name|getName
argument_list|()
operator|+
literal|"("
operator|+
name|msg
operator|+
literal|")"
argument_list|,
name|parentIdsByAttrValue
operator|.
name|get
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|c
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|msg
argument_list|,
name|parentIdsByAttrValue
operator|.
name|size
argument_list|()
argument_list|,
name|color_s
operator|.
name|getValueCount
argument_list|()
operator|+
name|size_s
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
comment|//System.out.println(parentIdsByAttrValue);
block|}
DECL|method|query
specifier|private
name|QueryResponse
name|query
parameter_list|(
name|String
modifier|...
name|arg
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|ModifiableSolrParams
name|solrParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|arg
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|solrParams
operator|.
name|add
argument_list|(
name|arg
index|[
name|i
index|]
argument_list|,
name|arg
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|query
argument_list|(
name|collection
argument_list|,
name|solrParams
argument_list|)
return|;
block|}
DECL|method|indexDocs
specifier|private
name|void
name|indexDocs
parameter_list|(
name|Collection
argument_list|<
name|SolrInputDocument
argument_list|>
name|pdocs
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|cluster
operator|.
name|getSolrClient
argument_list|()
operator|.
name|add
argument_list|(
name|collection
argument_list|,
name|pdocs
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

