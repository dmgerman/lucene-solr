begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
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
name|analysis
operator|.
name|TokenStream
import|;
end_import

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
name|Iterator
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

begin_comment
comment|/**  * @version $Id$  */
end_comment

begin_class
DECL|class|TestSynonymFilter
specifier|public
class|class
name|TestSynonymFilter
extends|extends
name|BaseTokenTestCase
block|{
DECL|method|strings
specifier|public
name|List
name|strings
parameter_list|(
name|String
name|str
parameter_list|)
block|{
name|String
index|[]
name|arr
init|=
name|str
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|arr
argument_list|)
return|;
block|}
DECL|method|getTokList
specifier|public
name|List
argument_list|<
name|Token
argument_list|>
name|getTokList
parameter_list|(
name|SynonymMap
name|dict
parameter_list|,
name|String
name|input
parameter_list|,
name|boolean
name|includeOrig
parameter_list|)
throws|throws
name|IOException
block|{
name|ArrayList
argument_list|<
name|Token
argument_list|>
name|lst
init|=
operator|new
name|ArrayList
argument_list|<
name|Token
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
name|toks
init|=
name|tokens
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|TokenStream
name|ts
init|=
operator|new
name|TokenStream
argument_list|()
block|{
name|Iterator
name|iter
init|=
name|toks
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|iter
operator|.
name|hasNext
argument_list|()
condition|?
operator|(
name|Token
operator|)
name|iter
operator|.
name|next
argument_list|()
else|:
literal|null
return|;
block|}
block|}
decl_stmt|;
name|SynonymFilter
name|sf
init|=
operator|new
name|SynonymFilter
argument_list|(
name|ts
argument_list|,
name|dict
argument_list|)
decl_stmt|;
name|Token
name|target
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
comment|// test with token reuse
while|while
condition|(
literal|true
condition|)
block|{
name|Token
name|t
init|=
name|sf
operator|.
name|next
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
condition|)
return|return
name|lst
return|;
name|lst
operator|.
name|add
argument_list|(
operator|(
name|Token
operator|)
name|t
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testMatching
specifier|public
name|void
name|testMatching
parameter_list|()
throws|throws
name|IOException
block|{
name|SynonymMap
name|map
init|=
operator|new
name|SynonymMap
argument_list|()
decl_stmt|;
name|boolean
name|orig
init|=
literal|false
decl_stmt|;
name|boolean
name|merge
init|=
literal|true
decl_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"a b"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"ab"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"a c"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"ac"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"aa"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"b"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"bb"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"z x c v"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"zxcv"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"x c"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"xc"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
comment|// System.out.println(map);
comment|// System.out.println(getTokList(map,"a",false));
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"$"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"$"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"a"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"aa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"a $"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"aa $"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"$ a"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"$ aa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"a a"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"aa aa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"b"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"bb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"z x c v"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"zxcv"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"z x c $"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"z xc $"
argument_list|)
argument_list|)
expr_stmt|;
comment|// repeats
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"a b"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"ab"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"a b"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"ab"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"a b"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"ab"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check for lack of recursion
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"zoo"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"zoo"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"zoo zoo $ zoo"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"zoo zoo $ zoo"
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"zoo"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"zoo zoo"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"zoo zoo $ zoo"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"zoo zoo zoo zoo $ zoo zoo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIncludeOrig
specifier|public
name|void
name|testIncludeOrig
parameter_list|()
throws|throws
name|IOException
block|{
name|SynonymMap
name|map
init|=
operator|new
name|SynonymMap
argument_list|()
decl_stmt|;
name|boolean
name|orig
init|=
literal|true
decl_stmt|;
name|boolean
name|merge
init|=
literal|true
decl_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"a b"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"ab"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"a c"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"ac"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"aa"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"b"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"bb"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"z x c v"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"zxcv"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"x c"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"xc"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
comment|// System.out.println(map);
comment|// System.out.println(getTokList(map,"a",false));
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"$"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"$"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"a"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"a/aa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"a"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"a/aa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"$ a"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"$ a/aa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"a $"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"a/aa $"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"$ a !"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"$ a/aa !"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"a a"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"a/aa a/aa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"b"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"b/bb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"z x c v"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"z/zxcv x c v"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"z x c $"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"z x/xc c $"
argument_list|)
argument_list|)
expr_stmt|;
comment|// check for lack of recursion
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"zoo zoo"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"zoo"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"zoo zoo $ zoo"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"zoo/zoo zoo/zoo $ zoo/zoo"
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"zoo"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"zoo zoo"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"zoo zoo $ zoo"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"zoo/zoo zoo $ zoo/zoo zoo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMapMerge
specifier|public
name|void
name|testMapMerge
parameter_list|()
throws|throws
name|IOException
block|{
name|SynonymMap
name|map
init|=
operator|new
name|SynonymMap
argument_list|()
decl_stmt|;
name|boolean
name|orig
init|=
literal|false
decl_stmt|;
name|boolean
name|merge
init|=
literal|true
decl_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"a5,5"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"a3,3"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
comment|// System.out.println(map);
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"a"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"a3 a5,2"
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"b"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"b3,3"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"b"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"b5,5"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
comment|//System.out.println(map);
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"b"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"b3 b5,2"
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"A3,3"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"A5,5"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"a"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"a3/A3 a5,2/A5"
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"a1"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"a"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"a1 a3,2/A3 a5,2/A5"
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"a2,2"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"a4,4 a6,2"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"a"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"a1 a2 a3/A3 a4 a5/A5 a6"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testOverlap
specifier|public
name|void
name|testOverlap
parameter_list|()
throws|throws
name|IOException
block|{
name|SynonymMap
name|map
init|=
operator|new
name|SynonymMap
argument_list|()
decl_stmt|;
name|boolean
name|orig
init|=
literal|false
decl_stmt|;
name|boolean
name|merge
init|=
literal|true
decl_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"qwe"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"qq/ww/ee"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"qwe"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"xx"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"qwe"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"yy"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"qwe"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"zz"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"$"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"$"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"qwe"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"qq/ww/ee/xx/yy/zz"
argument_list|)
argument_list|)
expr_stmt|;
comment|// test merging within the map
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"a5,5 a8,3 a10,2"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"a3,3 a7,4 a9,2 a11,2 a111,100"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"a"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"a3 a5,2 a7,2 a8 a9 a10 a11 a111,100"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testOffsets
specifier|public
name|void
name|testOffsets
parameter_list|()
throws|throws
name|IOException
block|{
name|SynonymMap
name|map
init|=
operator|new
name|SynonymMap
argument_list|()
decl_stmt|;
name|boolean
name|orig
init|=
literal|false
decl_stmt|;
name|boolean
name|merge
init|=
literal|true
decl_stmt|;
comment|// test that generated tokens start at the same offset as the original
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"aa"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"a,5"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"aa,5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"a,0"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"aa,0"
argument_list|)
argument_list|)
expr_stmt|;
comment|// test that offset of first replacement is ignored (always takes the orig offset)
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"b"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"bb,100"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"b,5"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"bb,5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"b,0"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"bb,0"
argument_list|)
argument_list|)
expr_stmt|;
comment|// test that subsequent tokens are adjusted accordingly
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"c"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"cc,100 c2,2"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"c,5"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"cc,5 c2,2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"c,0"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"cc,0 c2,2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testOffsetsWithOrig
specifier|public
name|void
name|testOffsetsWithOrig
parameter_list|()
throws|throws
name|IOException
block|{
name|SynonymMap
name|map
init|=
operator|new
name|SynonymMap
argument_list|()
decl_stmt|;
name|boolean
name|orig
init|=
literal|true
decl_stmt|;
name|boolean
name|merge
init|=
literal|true
decl_stmt|;
comment|// test that generated tokens start at the same offset as the original
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"aa"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"a,5"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"a,5/aa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"a,0"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"a,0/aa"
argument_list|)
argument_list|)
expr_stmt|;
comment|// test that offset of first replacement is ignored (always takes the orig offset)
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"b"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"bb,100"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"b,5"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"bb,5/b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"b,0"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"bb,0/b"
argument_list|)
argument_list|)
expr_stmt|;
comment|// test that subsequent tokens are adjusted accordingly
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"c"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"cc,100 c2,2"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"c,5"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"cc,5/c c2,2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokEqual
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"c,0"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"cc,0/c c2,2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testOffsetBug
specifier|public
name|void
name|testOffsetBug
parameter_list|()
throws|throws
name|IOException
block|{
comment|// With the following rules:
comment|// a a=>b
comment|// x=>y
comment|// analysing "a x" causes "y" to have a bad offset (end less than start)
comment|// SOLR-167
name|SynonymMap
name|map
init|=
operator|new
name|SynonymMap
argument_list|()
decl_stmt|;
name|boolean
name|orig
init|=
literal|false
decl_stmt|;
name|boolean
name|merge
init|=
literal|true
decl_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"a a"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"b"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|map
operator|.
name|add
argument_list|(
name|strings
argument_list|(
literal|"x"
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"y"
argument_list|)
argument_list|,
name|orig
argument_list|,
name|merge
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"a,1,0,1 a,1,2,3 x,1,4,5"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// "a a x" => "b y"
name|assertTokEqualOff
argument_list|(
name|getTokList
argument_list|(
name|map
argument_list|,
literal|"a,1,0,1 a,1,2,3 x,1,4,5"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|tokens
argument_list|(
literal|"b,1,0,3 y,1,4,5"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

