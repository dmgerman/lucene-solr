begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
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
name|util
operator|.
name|*
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

begin_comment
comment|/**  * Tests some basic functionality of the DisMaxRequestHandler  */
end_comment

begin_class
DECL|class|DisMaxRequestHandlerTest
specifier|public
class|class
name|DisMaxRequestHandlerTest
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
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
name|lrf
operator|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"dismax"
argument_list|,
literal|0
argument_list|,
literal|20
argument_list|,
literal|"version"
argument_list|,
literal|"2.0"
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"t_s"
argument_list|)
expr_stmt|;
block|}
comment|/** Add some documents to the index */
DECL|method|populate
specifier|protected
name|void
name|populate
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"666"
argument_list|,
literal|"features_t"
argument_list|,
literal|"cool and scary stuff"
argument_list|,
literal|"subject"
argument_list|,
literal|"traveling in hell"
argument_list|,
literal|"t_s"
argument_list|,
literal|"movie"
argument_list|,
literal|"title"
argument_list|,
literal|"The Omen"
argument_list|,
literal|"weight"
argument_list|,
literal|"87.9"
argument_list|,
literal|"iind"
argument_list|,
literal|"666"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"features_t"
argument_list|,
literal|"cool stuff"
argument_list|,
literal|"subject"
argument_list|,
literal|"traveling the galaxy"
argument_list|,
literal|"t_s"
argument_list|,
literal|"movie"
argument_list|,
literal|"t_s"
argument_list|,
literal|"book"
argument_list|,
literal|"title"
argument_list|,
literal|"Hitch Hiker's Guide to the Galaxy"
argument_list|,
literal|"weight"
argument_list|,
literal|"99.45"
argument_list|,
literal|"iind"
argument_list|,
literal|"42"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"features_t"
argument_list|,
literal|"nothing"
argument_list|,
literal|"subject"
argument_list|,
literal|"garbage"
argument_list|,
literal|"t_s"
argument_list|,
literal|"book"
argument_list|,
literal|"title"
argument_list|,
literal|"Most Boring Guide Ever"
argument_list|,
literal|"weight"
argument_list|,
literal|"77"
argument_list|,
literal|"iind"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"8675309"
argument_list|,
literal|"features_t"
argument_list|,
literal|"Wikedly memorable chorus and stuff"
argument_list|,
literal|"subject"
argument_list|,
literal|"One Cool Hot Chick"
argument_list|,
literal|"t_s"
argument_list|,
literal|"song"
argument_list|,
literal|"title"
argument_list|,
literal|"Jenny"
argument_list|,
literal|"weight"
argument_list|,
literal|"97.3"
argument_list|,
literal|"iind"
argument_list|,
literal|"8675309"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSomeStuff
specifier|public
name|void
name|testSomeStuff
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestSomeStuff
argument_list|(
literal|"dismax"
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestSomeStuff
specifier|public
name|void
name|doTestSomeStuff
parameter_list|(
specifier|final
name|String
name|qt
parameter_list|)
throws|throws
name|Exception
block|{
name|populate
argument_list|()
expr_stmt|;
name|assertQ
argument_list|(
literal|"basic match"
argument_list|,
name|req
argument_list|(
literal|"guide"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|,
literal|"//lst[@name='facet_fields']/lst[@name='t_s']"
argument_list|,
literal|"*[count(//lst[@name='t_s']/int)=3]"
argument_list|,
literal|"//lst[@name='t_s']/int[@name='book'][.='2']"
argument_list|,
literal|"//lst[@name='t_s']/int[@name='movie'][.='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"basic cross field matching, boost on same field matching"
argument_list|,
name|req
argument_list|(
literal|"cool stuff"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='42']"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.='666']"
argument_list|,
literal|"//result/doc[3]/int[@name='id'][.='8675309']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"multi qf"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cool"
argument_list|,
literal|"qt"
argument_list|,
name|qt
argument_list|,
literal|"version"
argument_list|,
literal|"2.0"
argument_list|,
literal|"qf"
argument_list|,
literal|"subject"
argument_list|,
literal|"qf"
argument_list|,
literal|"features_t"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"boost query"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cool stuff"
argument_list|,
literal|"qt"
argument_list|,
name|qt
argument_list|,
literal|"version"
argument_list|,
literal|"2.0"
argument_list|,
literal|"bq"
argument_list|,
literal|"subject:hell^400"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='666']"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.='42']"
argument_list|,
literal|"//result/doc[3]/int[@name='id'][.='8675309']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"multi boost query"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cool stuff"
argument_list|,
literal|"qt"
argument_list|,
name|qt
argument_list|,
literal|"version"
argument_list|,
literal|"2.0"
argument_list|,
literal|"bq"
argument_list|,
literal|"subject:hell^400"
argument_list|,
literal|"bq"
argument_list|,
literal|"subject:cool^4"
argument_list|,
literal|"debugQuery"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][.='666']"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][.='8675309']"
argument_list|,
literal|"//result/doc[3]/int[@name='id'][.='42']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"minimum mm is three"
argument_list|,
name|req
argument_list|(
literal|"cool stuff traveling"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|,
literal|"//result/doc[1]/int[@name='id'][. ='42']"
argument_list|,
literal|"//result/doc[2]/int[@name='id'][. ='666']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"at 4 mm allows one missing "
argument_list|,
name|req
argument_list|(
literal|"cool stuff traveling jenny"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"relying on ALTQ from config"
argument_list|,
name|req
argument_list|(
literal|"qt"
argument_list|,
name|qt
argument_list|,
literal|"fq"
argument_list|,
literal|"id:666"
argument_list|,
literal|"facet"
argument_list|,
literal|"false"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"explicit ALTQ"
argument_list|,
name|req
argument_list|(
literal|"qt"
argument_list|,
name|qt
argument_list|,
literal|"q.alt"
argument_list|,
literal|"id:9999"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:666"
argument_list|,
literal|"facet"
argument_list|,
literal|"false"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"no query slop == no match"
argument_list|,
name|req
argument_list|(
literal|"qt"
argument_list|,
name|qt
argument_list|,
literal|"q"
argument_list|,
literal|"\"cool chick\""
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"query slop == match"
argument_list|,
name|req
argument_list|(
literal|"qt"
argument_list|,
name|qt
argument_list|,
literal|"qs"
argument_list|,
literal|"2"
argument_list|,
literal|"q"
argument_list|,
literal|"\"cool chick\""
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testExtraBlankBQ
specifier|public
name|void
name|testExtraBlankBQ
parameter_list|()
throws|throws
name|Exception
block|{
name|populate
argument_list|()
expr_stmt|;
comment|// if the boost queries are in their own boolean query, the clauses will be
comment|// surrounded by ()'s in the debug output
name|Pattern
name|p
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"subject:hell\\s*subject:cool"
argument_list|)
decl_stmt|;
name|Pattern
name|p_bool
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\(subject:hell\\s*subject:cool\\)"
argument_list|)
decl_stmt|;
name|String
name|resp
init|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cool stuff"
argument_list|,
literal|"qt"
argument_list|,
literal|"dismax"
argument_list|,
literal|"version"
argument_list|,
literal|"2.0"
argument_list|,
literal|"bq"
argument_list|,
literal|"subject:hell OR subject:cool"
argument_list|,
literal|"debugQuery"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|p
operator|.
name|matcher
argument_list|(
name|resp
argument_list|)
operator|.
name|find
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|p_bool
operator|.
name|matcher
argument_list|(
name|resp
argument_list|)
operator|.
name|find
argument_list|()
argument_list|)
expr_stmt|;
name|resp
operator|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"cool stuff"
argument_list|,
literal|"qt"
argument_list|,
literal|"dismax"
argument_list|,
literal|"version"
argument_list|,
literal|"2.0"
argument_list|,
literal|"bq"
argument_list|,
literal|"subject:hell OR subject:cool"
argument_list|,
literal|"bq"
argument_list|,
literal|""
argument_list|,
literal|"debugQuery"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|p
operator|.
name|matcher
argument_list|(
name|resp
argument_list|)
operator|.
name|find
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|p_bool
operator|.
name|matcher
argument_list|(
name|resp
argument_list|)
operator|.
name|find
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOldStyleDefaults
specifier|public
name|void
name|testOldStyleDefaults
parameter_list|()
throws|throws
name|Exception
block|{
name|lrf
operator|=
name|h
operator|.
name|getRequestFactory
argument_list|(
literal|"dismaxOldStyleDefaults"
argument_list|,
literal|0
argument_list|,
literal|20
argument_list|,
literal|"version"
argument_list|,
literal|"2.0"
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"t_s"
argument_list|)
expr_stmt|;
name|doTestSomeStuff
argument_list|(
literal|"dismaxOldStyleDefaults"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

