begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_comment
comment|//Unlike TestSolr4Spatial, not parameterized / not generic.
end_comment

begin_class
DECL|class|TestSolr4Spatial2
specifier|public
class|class
name|TestSolr4Spatial2
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-spatial.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBBox
specifier|public
name|void
name|testBBox
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|fieldName
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"bbox"
else|:
literal|"bboxD_dynamic"
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
comment|//nothing
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|fieldName
argument_list|,
literal|"ENVELOPE(-10, 20, 15, 10)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
name|fieldName
argument_list|,
literal|"ENVELOPE(22, 22, 10, 10)"
argument_list|)
argument_list|)
expr_stmt|;
comment|//pt
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!field f="
operator|+
name|fieldName
operator|+
literal|" filter=false score=overlapRatio "
operator|+
literal|"queryTargetProportion=0.25}"
operator|+
literal|"Intersects(ENVELOPE(10,25,12,10))"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"debug"
argument_list|,
literal|"results"
argument_list|)
argument_list|,
comment|//explain info
literal|"/response/docs/[0]/id=='2'"
argument_list|,
literal|"/response/docs/[0]/score==0.75]"
argument_list|,
literal|"/response/docs/[1]/id=='1'"
argument_list|,
literal|"/response/docs/[1]/score==0.26666668]"
argument_list|,
literal|"/response/docs/[2]/id=='0'"
argument_list|,
literal|"/response/docs/[2]/score==0.0"
argument_list|,
literal|"/response/docs/[1]/"
operator|+
name|fieldName
operator|+
literal|"=='ENVELOPE(-10, 20, 15, 10)'"
comment|//stored value
argument_list|)
expr_stmt|;
comment|//minSideLength with point query
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!field f="
operator|+
name|fieldName
operator|+
literal|" filter=false score=overlapRatio "
operator|+
literal|"queryTargetProportion=0.5 minSideLength=1}"
operator|+
literal|"Intersects(ENVELOPE(0,0,12,12))"
argument_list|,
comment|//pt
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|,
literal|"debug"
argument_list|,
literal|"results"
argument_list|)
argument_list|,
comment|//explain info
literal|"/response/docs/[0]/id=='1'"
argument_list|,
literal|"/response/docs/[0]/score==0.50333333]"
comment|//just over 0.5
argument_list|)
expr_stmt|;
comment|//area2D
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!field f="
operator|+
name|fieldName
operator|+
literal|" filter=false score=area2D}"
operator|+
literal|"Intersects(ENVELOPE(0,0,12,12))"
argument_list|,
comment|//pt
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|,
literal|"debug"
argument_list|,
literal|"results"
argument_list|)
argument_list|,
comment|//explain info
literal|"/response/docs/[0]/id=='1'"
argument_list|,
literal|"/response/docs/[0]/score=="
operator|+
operator|(
literal|30f
operator|*
literal|5f
operator|)
operator|+
literal|"]"
comment|//150
argument_list|)
expr_stmt|;
comment|//area (not 2D)
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!field f="
operator|+
name|fieldName
operator|+
literal|" filter=false score=area}"
operator|+
literal|"Intersects(ENVELOPE(0,0,12,12))"
argument_list|,
comment|//pt
literal|"fl"
argument_list|,
literal|"id,score"
argument_list|,
literal|"debug"
argument_list|,
literal|"results"
argument_list|)
argument_list|,
comment|//explain info
literal|"/response/docs/[0]/id=='1'"
argument_list|,
literal|"/response/docs/[0]/score=="
operator|+
literal|146.39793f
operator|+
literal|"]"
comment|//a bit less than 150
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

