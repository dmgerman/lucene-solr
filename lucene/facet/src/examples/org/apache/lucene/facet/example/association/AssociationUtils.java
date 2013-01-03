begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.example.association
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|example
operator|.
name|association
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|associations
operator|.
name|CategoryAssociation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|associations
operator|.
name|CategoryFloatAssociation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|associations
operator|.
name|CategoryIntAssociation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * @lucene.experimental  */
end_comment

begin_class
DECL|class|AssociationUtils
specifier|public
class|class
name|AssociationUtils
block|{
comment|/**    * Categories: categories[D][N] == category-path with association no. N for    * document no. D.    */
DECL|field|categories
specifier|public
specifier|static
name|CategoryPath
index|[]
index|[]
name|categories
init|=
block|{
comment|// Doc #1
block|{
operator|new
name|CategoryPath
argument_list|(
literal|"tags"
argument_list|,
literal|"lucene"
argument_list|)
block|,
operator|new
name|CategoryPath
argument_list|(
literal|"genre"
argument_list|,
literal|"computing"
argument_list|)
block|}
block|,
comment|// Doc #2
block|{
operator|new
name|CategoryPath
argument_list|(
literal|"tags"
argument_list|,
literal|"lucene"
argument_list|)
block|,
operator|new
name|CategoryPath
argument_list|(
literal|"tags"
argument_list|,
literal|"solr"
argument_list|)
block|,
operator|new
name|CategoryPath
argument_list|(
literal|"genre"
argument_list|,
literal|"computing"
argument_list|)
block|,
operator|new
name|CategoryPath
argument_list|(
literal|"genre"
argument_list|,
literal|"software"
argument_list|)
block|}
block|}
decl_stmt|;
DECL|field|associations
specifier|public
specifier|static
name|CategoryAssociation
index|[]
index|[]
name|associations
init|=
block|{
comment|// Doc #1 associations
block|{
comment|/* 3 occurrences for tag 'lucene' */
operator|new
name|CategoryIntAssociation
argument_list|(
literal|3
argument_list|)
block|,
comment|/* 87% confidence level of genre 'computing' */
operator|new
name|CategoryFloatAssociation
argument_list|(
literal|0.87f
argument_list|)
block|}
block|,
comment|// Doc #2 associations
block|{
comment|/* 1 occurrence for tag 'lucene' */
operator|new
name|CategoryIntAssociation
argument_list|(
literal|1
argument_list|)
block|,
comment|/* 2 occurrences for tag 'solr' */
operator|new
name|CategoryIntAssociation
argument_list|(
literal|2
argument_list|)
block|,
comment|/* 75% confidence level of genre 'computing' */
operator|new
name|CategoryFloatAssociation
argument_list|(
literal|0.75f
argument_list|)
block|,
comment|/* 34% confidence level of genre 'software' */
operator|new
name|CategoryFloatAssociation
argument_list|(
literal|0.34f
argument_list|)
block|,     }
block|}
decl_stmt|;
block|}
end_class

end_unit

