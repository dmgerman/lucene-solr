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
DECL|class|TestPayloadCheckQParserPlugin
specifier|public
class|class
name|TestPayloadCheckQParserPlugin
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
literal|"solrconfig.xml"
argument_list|,
literal|"schema11.xml"
argument_list|)
expr_stmt|;
name|createIndex
argument_list|()
expr_stmt|;
block|}
DECL|method|createIndex
specifier|public
specifier|static
name|void
name|createIndex
parameter_list|()
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"vals_dpi"
argument_list|,
literal|"A|1 B|2 C|3"
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
literal|"vals_dpf"
argument_list|,
literal|"one|1.0 two|2.0 three|3.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"vals_dps"
argument_list|,
literal|"the|ARTICLE cat|NOUN jumped|VERB"
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
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
block|{
name|clearIndex
argument_list|()
expr_stmt|;
name|String
index|[]
name|should_matches
init|=
operator|new
name|String
index|[]
block|{
literal|"{!payload_check f=vals_dpi v=A payloads=1}"
block|,
literal|"{!payload_check f=vals_dpi v=B payloads=2}"
block|,
literal|"{!payload_check f=vals_dpi v=C payloads=3}"
block|,
literal|"{!payload_check f=vals_dpi payloads='1 2'}A B"
block|,
comment|// "{!payload_check f=vals_dpi payloads='1 2.0'}A B",  // ideally this should pass, but IntegerEncoder can't handle "2.0"
literal|"{!payload_check f=vals_dpi payloads='1 2 3'}A B C"
block|,
literal|"{!payload_check f=vals_dpf payloads='1 2'}one two"
block|,
literal|"{!payload_check f=vals_dpf payloads='1 2.0'}one two"
block|,
comment|// shows that FloatEncoder can handle "1"
literal|"{!payload_check f=vals_dps payloads='NOUN VERB'}cat jumped"
block|}
decl_stmt|;
name|String
index|[]
name|should_not_matches
init|=
operator|new
name|String
index|[]
block|{
literal|"{!payload_check f=vals_dpi v=A payloads=2}"
block|,
literal|"{!payload_check f=vals_dpi payloads='1 2'}B C"
block|,
literal|"{!payload_check f=vals_dpi payloads='1 2 3'}A B"
block|,
literal|"{!payload_check f=vals_dpi payloads='1 2'}A B C"
block|,
literal|"{!payload_check f=vals_dpf payloads='1 2.0'}two three"
block|,
literal|"{!payload_check f=vals_dps payloads='VERB NOUN'}cat jumped"
block|}
decl_stmt|;
for|for
control|(
name|String
name|should_match
range|:
name|should_matches
control|)
block|{
name|assertQ
argument_list|(
name|should_match
argument_list|,
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|should_match
argument_list|)
argument_list|,
literal|"//result[@numFound='1']"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|should_not_match
range|:
name|should_not_matches
control|)
block|{
name|assertQ
argument_list|(
name|should_not_match
argument_list|,
name|req
argument_list|(
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"q"
argument_list|,
name|should_not_match
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

