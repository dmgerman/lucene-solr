begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|lucene
operator|.
name|util
operator|.
name|TestUtil
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
name|After
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
name|ArrayList
import|;
end_import

begin_comment
comment|/** Inspired by LUCENE-5790 */
end_comment

begin_class
DECL|class|TestMissingGroups
specifier|public
class|class
name|TestMissingGroups
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeTests
specifier|public
specifier|static
name|void
name|beforeTests
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema15.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanup
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|Exception
block|{
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testGroupsOnMissingValues
specifier|public
name|void
name|testGroupsOnMissingValues
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|500
argument_list|)
decl_stmt|;
comment|// setup some key values for some random docs in our index
comment|// every other doc will have no values for these fields
comment|// NOTE: special values may be randomly assigned to the *same* docs
specifier|final
name|List
argument_list|<
name|SpecialField
argument_list|>
name|specials
init|=
operator|new
name|ArrayList
argument_list|<
name|SpecialField
argument_list|>
argument_list|(
literal|7
argument_list|)
decl_stmt|;
name|specials
operator|.
name|add
argument_list|(
operator|new
name|SpecialField
argument_list|(
name|numDocs
argument_list|,
literal|"group_s1"
argument_list|,
literal|"xxx"
argument_list|,
literal|"yyy"
argument_list|)
argument_list|)
expr_stmt|;
name|specials
operator|.
name|add
argument_list|(
operator|new
name|SpecialField
argument_list|(
name|numDocs
argument_list|,
literal|"group_ti"
argument_list|,
literal|"42"
argument_list|,
literal|"24"
argument_list|)
argument_list|)
expr_stmt|;
name|specials
operator|.
name|add
argument_list|(
operator|new
name|SpecialField
argument_list|(
name|numDocs
argument_list|,
literal|"group_td"
argument_list|,
literal|"34.56"
argument_list|,
literal|"12.78"
argument_list|)
argument_list|)
expr_stmt|;
name|specials
operator|.
name|add
argument_list|(
operator|new
name|SpecialField
argument_list|(
name|numDocs
argument_list|,
literal|"group_tl"
argument_list|,
literal|"66666666"
argument_list|,
literal|"999999999"
argument_list|)
argument_list|)
expr_stmt|;
name|specials
operator|.
name|add
argument_list|(
operator|new
name|SpecialField
argument_list|(
name|numDocs
argument_list|,
literal|"group_tf"
argument_list|,
literal|"56.78"
argument_list|,
literal|"78.45"
argument_list|)
argument_list|)
expr_stmt|;
name|specials
operator|.
name|add
argument_list|(
operator|new
name|SpecialField
argument_list|(
name|numDocs
argument_list|,
literal|"group_b"
argument_list|,
literal|"true"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
name|specials
operator|.
name|add
argument_list|(
operator|new
name|SpecialField
argument_list|(
name|numDocs
argument_list|,
literal|"group_tdt"
argument_list|,
literal|"2009-05-10T03:30:00Z"
argument_list|,
literal|"1976-03-06T15:06:00Z"
argument_list|)
argument_list|)
expr_stmt|;
comment|// build up our index of docs
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
comment|// NOTE: start at 1, doc#0 is below...
name|SolrInputDocument
name|d
init|=
name|sdoc
argument_list|(
literal|"id"
argument_list|,
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|SpecialField
operator|.
name|special_docids
operator|.
name|contains
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|d
operator|.
name|addField
argument_list|(
literal|"special_s"
argument_list|,
literal|"special"
argument_list|)
expr_stmt|;
for|for
control|(
name|SpecialField
name|f
range|:
name|specials
control|)
block|{
if|if
condition|(
name|f
operator|.
name|docX
operator|==
name|i
condition|)
block|{
name|d
operator|.
name|addField
argument_list|(
name|f
operator|.
name|field
argument_list|,
name|f
operator|.
name|valueX
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|f
operator|.
name|docY
operator|==
name|i
condition|)
block|{
name|d
operator|.
name|addField
argument_list|(
name|f
operator|.
name|field
argument_list|,
name|f
operator|.
name|valueY
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// doc isn't special, give it a random chances of being excluded from some queries
name|d
operator|.
name|addField
argument_list|(
literal|"filter_b"
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertU
argument_list|(
name|adoc
argument_list|(
name|d
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// mess with the segment counts
block|}
block|}
comment|// doc#0: at least one doc that is guaranteed not special and has no chance of being filtered
name|assertU
argument_list|(
name|adoc
argument_list|(
name|sdoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// sanity check
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|,
literal|"//result[@numFound="
operator|+
name|numDocs
operator|+
literal|"]"
argument_list|)
expr_stmt|;
for|for
control|(
name|SpecialField
name|special
range|:
name|specials
control|)
block|{
comment|// sanity checks
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!term f="
operator|+
name|special
operator|.
name|field
operator|+
literal|"}"
operator|+
name|special
operator|.
name|valueX
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!term f="
operator|+
name|special
operator|.
name|field
operator|+
literal|"}"
operator|+
name|special
operator|.
name|valueY
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
comment|// group on special field, and confirm all docs w/o group field get put into a single group
specifier|final
name|String
name|xpre
init|=
literal|"//lst[@name='grouped']/lst[@name='"
operator|+
name|special
operator|.
name|field
operator|+
literal|"']"
decl_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
operator|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"*:*"
else|:
literal|"special_s:special id:[0 TO 400]"
operator|)
argument_list|,
literal|"fq"
argument_list|,
operator|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"*:*"
else|:
literal|"-filter_b:"
operator|+
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
operator|)
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.field"
argument_list|,
name|special
operator|.
name|field
argument_list|,
literal|"group.ngroups"
argument_list|,
literal|"true"
argument_list|)
comment|// basic grouping checks
argument_list|,
name|xpre
operator|+
literal|"/int[@name='ngroups'][.='3']"
argument_list|,
name|xpre
operator|+
literal|"/arr[@name='groups'][count(lst)=3]"
comment|// sanity check one group is the missing values
argument_list|,
name|xpre
operator|+
literal|"/arr[@name='groups']/lst/null[@name='groupValue']"
comment|// check we have the correct groups for the special values with a single doc
argument_list|,
name|xpre
operator|+
literal|"/arr[@name='groups']/lst/*[@name='groupValue'][.='"
operator|+
name|special
operator|.
name|valueX
operator|+
literal|"']/following-sibling::result[@name='doclist'][@numFound=1]/doc/str[@name='id'][.="
operator|+
name|special
operator|.
name|docX
operator|+
literal|"]"
argument_list|,
name|xpre
operator|+
literal|"/arr[@name='groups']/lst/*[@name='groupValue'][.='"
operator|+
name|special
operator|.
name|valueY
operator|+
literal|"']/following-sibling::result[@name='doclist'][@numFound=1]/doc/str[@name='id'][.="
operator|+
name|special
operator|.
name|docY
operator|+
literal|"]"
argument_list|)
expr_stmt|;
comment|// now do the same check, but exclude one special doc to force only 2 groups
specifier|final
name|int
name|doc
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|special
operator|.
name|docX
else|:
name|special
operator|.
name|docY
decl_stmt|;
specifier|final
name|Object
name|val
init|=
operator|(
name|doc
operator|==
name|special
operator|.
name|docX
operator|)
condition|?
name|special
operator|.
name|valueX
else|:
name|special
operator|.
name|valueY
decl_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
operator|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"*:*"
else|:
literal|"special_s:special id:[0 TO 400]"
operator|)
argument_list|,
literal|"fq"
argument_list|,
operator|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"*:*"
else|:
literal|"-filter_b:"
operator|+
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
operator|)
argument_list|,
literal|"fq"
argument_list|,
literal|"-id:"
operator|+
operator|(
operator|(
name|doc
operator|==
name|special
operator|.
name|docX
operator|)
condition|?
name|special
operator|.
name|docY
else|:
name|special
operator|.
name|docX
operator|)
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.field"
argument_list|,
name|special
operator|.
name|field
argument_list|,
literal|"group.ngroups"
argument_list|,
literal|"true"
argument_list|)
comment|// basic grouping checks
argument_list|,
name|xpre
operator|+
literal|"/int[@name='ngroups'][.='2']"
argument_list|,
name|xpre
operator|+
literal|"/arr[@name='groups'][count(lst)=2]"
comment|// sanity check one group is the missing values
argument_list|,
name|xpre
operator|+
literal|"/arr[@name='groups']/lst/null[@name='groupValue']"
comment|// check we have the correct group for the special value with a single doc
argument_list|,
name|xpre
operator|+
literal|"/arr[@name='groups']/lst/*[@name='groupValue'][.='"
operator|+
name|val
operator|+
literal|"']/following-sibling::result[@name='doclist'][@numFound=1]/doc/str[@name='id'][.="
operator|+
name|doc
operator|+
literal|"]"
argument_list|)
expr_stmt|;
comment|// one last check, exclude both docs and verify the only group is the missing value group
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
operator|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"*:*"
else|:
literal|"special_s:special id:[0 TO 400]"
operator|)
argument_list|,
literal|"fq"
argument_list|,
operator|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"*:*"
else|:
literal|"-filter_b:"
operator|+
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
operator|)
argument_list|,
literal|"fq"
argument_list|,
literal|"-id:"
operator|+
name|special
operator|.
name|docX
argument_list|,
literal|"fq"
argument_list|,
literal|"-id:"
operator|+
name|special
operator|.
name|docY
argument_list|,
literal|"group"
argument_list|,
literal|"true"
argument_list|,
literal|"group.field"
argument_list|,
name|special
operator|.
name|field
argument_list|,
literal|"group.ngroups"
argument_list|,
literal|"true"
argument_list|)
comment|// basic grouping checks
argument_list|,
name|xpre
operator|+
literal|"/int[@name='ngroups'][.='1']"
argument_list|,
name|xpre
operator|+
literal|"/arr[@name='groups'][count(lst)=1]"
comment|// the only group should be the missing values
argument_list|,
name|xpre
operator|+
literal|"/arr[@name='groups']/lst/null[@name='groupValue']"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|SpecialField
specifier|private
specifier|static
specifier|final
class|class
name|SpecialField
block|{
comment|// fast lookup of which docs are special
DECL|field|special_docids
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|special_docids
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|field
specifier|public
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|docX
specifier|public
specifier|final
name|int
name|docX
decl_stmt|;
DECL|field|valueX
specifier|public
specifier|final
name|Object
name|valueX
decl_stmt|;
DECL|field|docY
specifier|public
specifier|final
name|int
name|docY
decl_stmt|;
DECL|field|valueY
specifier|public
specifier|final
name|Object
name|valueY
decl_stmt|;
DECL|method|SpecialField
specifier|public
name|SpecialField
parameter_list|(
name|int
name|numDocs
parameter_list|,
name|String
name|field
parameter_list|,
name|Object
name|valueX
parameter_list|,
name|Object
name|valueY
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|valueX
operator|=
name|valueX
expr_stmt|;
name|this
operator|.
name|valueY
operator|=
name|valueY
expr_stmt|;
name|this
operator|.
name|docX
operator|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|numDocs
operator|-
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|docY
operator|=
operator|(
name|docX
operator|<
operator|(
name|numDocs
operator|/
literal|2
operator|)
operator|)
condition|?
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|docX
operator|+
literal|1
argument_list|,
name|numDocs
operator|-
literal|1
argument_list|)
else|:
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|docX
operator|-
literal|1
argument_list|)
expr_stmt|;
name|special_docids
operator|.
name|add
argument_list|(
name|docX
argument_list|)
expr_stmt|;
name|special_docids
operator|.
name|add
argument_list|(
name|docY
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

