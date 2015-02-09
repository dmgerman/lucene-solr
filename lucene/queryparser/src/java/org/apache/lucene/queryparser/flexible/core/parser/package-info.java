begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**   * Necessary interfaces to implement text parsers.  *   *<h2>Parser</h2>  *<p>  * The package<tt>org.apache.lucene.queryparser.flexible.parser</tt> contains interfaces  * that should be implemented by the parsers.  *   * Parsers produce QueryNode Trees from a string object.  * These package still needs some work to add support to for multiple parsers.  *   * Features that should be supported for the future, related with the parser:  * - QueryNode tree should be able convertible to any parser syntax.  * - The query syntax should support calling other parsers.  * - QueryNode tree created by multiple parsers.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.flexible.core.parser
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|parser
package|;
end_package

end_unit

