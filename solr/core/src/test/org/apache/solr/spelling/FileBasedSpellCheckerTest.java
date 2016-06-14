begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.spelling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|Map
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
name|analysis
operator|.
name|Token
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
name|LuceneTestCase
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
name|LuceneTestCase
operator|.
name|SuppressTempFileChecks
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
name|util
operator|.
name|NamedList
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
name|core
operator|.
name|SolrCore
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
name|search
operator|.
name|SolrIndexSearcher
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
name|util
operator|.
name|RefCounted
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
comment|/**  *  * @since solr 1.3  **/
end_comment

begin_class
annotation|@
name|SuppressTempFileChecks
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/SOLR-1877 Spellcheck IndexReader leak bug?"
argument_list|)
DECL|class|FileBasedSpellCheckerTest
specifier|public
class|class
name|FileBasedSpellCheckerTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|queryConverter
specifier|private
specifier|static
name|SpellingQueryConverter
name|queryConverter
decl_stmt|;
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
literal|"schema.xml"
argument_list|)
expr_stmt|;
comment|//Index something with a title
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|,
literal|"teststop"
argument_list|,
literal|"This is a title"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"teststop"
argument_list|,
literal|"The quick reb fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"teststop"
argument_list|,
literal|"This is a Solr"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"teststop"
argument_list|,
literal|"solr foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|commit
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|queryConverter
operator|=
operator|new
name|SimpleQueryConverter
argument_list|()
expr_stmt|;
name|queryConverter
operator|.
name|init
argument_list|(
operator|new
name|NamedList
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
block|{
name|queryConverter
operator|=
literal|null
expr_stmt|;
block|}
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
name|FileBasedSpellChecker
name|checker
init|=
operator|new
name|FileBasedSpellChecker
argument_list|()
decl_stmt|;
name|NamedList
name|spellchecker
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
literal|"classname"
argument_list|,
name|FileBasedSpellChecker
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|SolrSpellChecker
operator|.
name|DICTIONARY_NAME
argument_list|,
literal|"external"
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|AbstractLuceneSpellChecker
operator|.
name|LOCATION
argument_list|,
literal|"spellings.txt"
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|AbstractLuceneSpellChecker
operator|.
name|FIELD
argument_list|,
literal|"teststop"
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|FileBasedSpellChecker
operator|.
name|SOURCE_FILE_CHAR_ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|File
name|indexDir
init|=
name|createTempDir
argument_list|(
name|LuceneTestCase
operator|.
name|getTestClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|AbstractLuceneSpellChecker
operator|.
name|INDEX_DIR
argument_list|,
name|indexDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|String
name|dictName
init|=
name|checker
operator|.
name|init
argument_list|(
name|spellchecker
argument_list|,
name|core
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dictName
operator|+
literal|" is not equal to "
operator|+
literal|"external"
argument_list|,
name|dictName
operator|.
name|equals
argument_list|(
literal|"external"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|checker
operator|.
name|build
argument_list|(
name|core
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|searcher
init|=
name|core
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
init|=
name|queryConverter
operator|.
name|convert
argument_list|(
literal|"fob"
argument_list|)
decl_stmt|;
name|SpellingOptions
name|spellOpts
init|=
operator|new
name|SpellingOptions
argument_list|(
name|tokens
argument_list|,
name|searcher
operator|.
name|get
argument_list|()
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
name|SpellingResult
name|result
init|=
name|checker
operator|.
name|getSuggestions
argument_list|(
name|spellOpts
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"result is null and it shouldn't be"
argument_list|,
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|suggestions
init|=
name|result
operator|.
name|get
argument_list|(
name|tokens
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|entry
init|=
name|suggestions
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|" is not equal to "
operator|+
literal|"foo"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
literal|"foo"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|+
literal|" does not equal: "
operator|+
name|SpellingResult
operator|.
name|NO_FREQUENCY_INFO
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|==
name|SpellingResult
operator|.
name|NO_FREQUENCY_INFO
argument_list|)
expr_stmt|;
name|spellOpts
operator|.
name|tokens
operator|=
name|queryConverter
operator|.
name|convert
argument_list|(
literal|"super"
argument_list|)
expr_stmt|;
name|result
operator|=
name|checker
operator|.
name|getSuggestions
argument_list|(
name|spellOpts
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"result is null and it shouldn't be"
argument_list|,
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|suggestions
operator|=
name|result
operator|.
name|get
argument_list|(
name|tokens
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"suggestions is not null and it should be"
argument_list|,
name|suggestions
operator|==
literal|null
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFieldType
specifier|public
name|void
name|testFieldType
parameter_list|()
throws|throws
name|Exception
block|{
name|FileBasedSpellChecker
name|checker
init|=
operator|new
name|FileBasedSpellChecker
argument_list|()
decl_stmt|;
name|NamedList
name|spellchecker
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
literal|"classname"
argument_list|,
name|FileBasedSpellChecker
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|SolrSpellChecker
operator|.
name|DICTIONARY_NAME
argument_list|,
literal|"external"
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|AbstractLuceneSpellChecker
operator|.
name|LOCATION
argument_list|,
literal|"spellings.txt"
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|AbstractLuceneSpellChecker
operator|.
name|FIELD
argument_list|,
literal|"teststop"
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|FileBasedSpellChecker
operator|.
name|SOURCE_FILE_CHAR_ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|File
name|indexDir
init|=
name|createTempDir
argument_list|()
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|indexDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|AbstractLuceneSpellChecker
operator|.
name|INDEX_DIR
argument_list|,
name|indexDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|SolrSpellChecker
operator|.
name|FIELD_TYPE
argument_list|,
literal|"teststop"
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|AbstractLuceneSpellChecker
operator|.
name|SPELLCHECKER_ARG_NAME
argument_list|,
name|spellchecker
argument_list|)
expr_stmt|;
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|String
name|dictName
init|=
name|checker
operator|.
name|init
argument_list|(
name|spellchecker
argument_list|,
name|core
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dictName
operator|+
literal|" is not equal to "
operator|+
literal|"external"
argument_list|,
name|dictName
operator|.
name|equals
argument_list|(
literal|"external"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|checker
operator|.
name|build
argument_list|(
name|core
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|searcher
init|=
name|core
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
init|=
name|queryConverter
operator|.
name|convert
argument_list|(
literal|"Solar"
argument_list|)
decl_stmt|;
name|SpellingOptions
name|spellOpts
init|=
operator|new
name|SpellingOptions
argument_list|(
name|tokens
argument_list|,
name|searcher
operator|.
name|get
argument_list|()
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
name|SpellingResult
name|result
init|=
name|checker
operator|.
name|getSuggestions
argument_list|(
name|spellOpts
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"result is null and it shouldn't be"
argument_list|,
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//should be lowercased, b/c we are using a lowercasing analyzer
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|suggestions
init|=
name|result
operator|.
name|get
argument_list|(
name|tokens
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"suggestions Size: "
operator|+
name|suggestions
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|1
argument_list|,
name|suggestions
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|entry
init|=
name|suggestions
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|" is not equal to "
operator|+
literal|"solr"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
literal|"solr"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|+
literal|" does not equal: "
operator|+
name|SpellingResult
operator|.
name|NO_FREQUENCY_INFO
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|==
name|SpellingResult
operator|.
name|NO_FREQUENCY_INFO
argument_list|)
expr_stmt|;
comment|//test something not in the spell checker
name|spellOpts
operator|.
name|tokens
operator|=
name|queryConverter
operator|.
name|convert
argument_list|(
literal|"super"
argument_list|)
expr_stmt|;
name|result
operator|=
name|checker
operator|.
name|getSuggestions
argument_list|(
name|spellOpts
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"result is null and it shouldn't be"
argument_list|,
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|suggestions
operator|=
name|result
operator|.
name|get
argument_list|(
name|tokens
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"suggestions is not null and it should be"
argument_list|,
name|suggestions
operator|==
literal|null
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
comment|/**    * No indexDir location set    */
annotation|@
name|Test
DECL|method|testRAMDirectory
specifier|public
name|void
name|testRAMDirectory
parameter_list|()
throws|throws
name|Exception
block|{
name|FileBasedSpellChecker
name|checker
init|=
operator|new
name|FileBasedSpellChecker
argument_list|()
decl_stmt|;
name|NamedList
name|spellchecker
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
literal|"classname"
argument_list|,
name|FileBasedSpellChecker
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|SolrSpellChecker
operator|.
name|DICTIONARY_NAME
argument_list|,
literal|"external"
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|AbstractLuceneSpellChecker
operator|.
name|LOCATION
argument_list|,
literal|"spellings.txt"
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|FileBasedSpellChecker
operator|.
name|SOURCE_FILE_CHAR_ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|AbstractLuceneSpellChecker
operator|.
name|FIELD
argument_list|,
literal|"teststop"
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|SolrSpellChecker
operator|.
name|FIELD_TYPE
argument_list|,
literal|"teststop"
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|AbstractLuceneSpellChecker
operator|.
name|SPELLCHECKER_ARG_NAME
argument_list|,
name|spellchecker
argument_list|)
expr_stmt|;
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|String
name|dictName
init|=
name|checker
operator|.
name|init
argument_list|(
name|spellchecker
argument_list|,
name|core
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dictName
operator|+
literal|" is not equal to "
operator|+
literal|"external"
argument_list|,
name|dictName
operator|.
name|equals
argument_list|(
literal|"external"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|checker
operator|.
name|build
argument_list|(
name|core
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|searcher
init|=
name|core
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
init|=
name|queryConverter
operator|.
name|convert
argument_list|(
literal|"solar"
argument_list|)
decl_stmt|;
name|SpellingOptions
name|spellOpts
init|=
operator|new
name|SpellingOptions
argument_list|(
name|tokens
argument_list|,
name|searcher
operator|.
name|get
argument_list|()
operator|.
name|getIndexReader
argument_list|()
argument_list|)
decl_stmt|;
name|SpellingResult
name|result
init|=
name|checker
operator|.
name|getSuggestions
argument_list|(
name|spellOpts
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"result is null and it shouldn't be"
argument_list|,
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//should be lowercased, b/c we are using a lowercasing analyzer
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|suggestions
init|=
name|result
operator|.
name|get
argument_list|(
name|tokens
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"suggestions Size: "
operator|+
name|suggestions
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|1
argument_list|,
name|suggestions
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|entry
init|=
name|suggestions
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|" is not equal to "
operator|+
literal|"solr"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
literal|"solr"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|+
literal|" does not equal: "
operator|+
name|SpellingResult
operator|.
name|NO_FREQUENCY_INFO
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|==
name|SpellingResult
operator|.
name|NO_FREQUENCY_INFO
argument_list|)
expr_stmt|;
name|spellOpts
operator|.
name|tokens
operator|=
name|queryConverter
operator|.
name|convert
argument_list|(
literal|"super"
argument_list|)
expr_stmt|;
name|result
operator|=
name|checker
operator|.
name|getSuggestions
argument_list|(
name|spellOpts
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"result is null and it shouldn't be"
argument_list|,
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|suggestions
operator|=
name|result
operator|.
name|get
argument_list|(
name|spellOpts
operator|.
name|tokens
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"suggestions size should be 0"
argument_list|,
name|suggestions
operator|.
name|size
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

