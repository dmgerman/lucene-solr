begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|lucli
package|package
name|lucli
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|gnu
operator|.
name|readline
operator|.
name|*
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
name|queryParser
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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

begin_comment
comment|/**  * lucli Main class for lucli: the Lucene Command Line Interface  * This class handles mostly the actual CLI part, command names, help, etc.  */
end_comment

begin_class
DECL|class|Lucli
specifier|public
class|class
name|Lucli
block|{
DECL|field|DEFAULT_INDEX
specifier|final
specifier|static
name|String
name|DEFAULT_INDEX
init|=
literal|"index"
decl_stmt|;
comment|//directory "index" under the current directory
DECL|field|HISTORYFILE
specifier|final
specifier|static
name|String
name|HISTORYFILE
init|=
literal|".lucli"
decl_stmt|;
comment|//history file in user's home directory
DECL|field|MAX_TERMS
specifier|public
specifier|final
specifier|static
name|int
name|MAX_TERMS
init|=
literal|100
decl_stmt|;
comment|//Maximum number of terms we're going to show
comment|// List of commands
comment|// To add another command, add it in here, in the list of addcomand(), and in the switch statement
DECL|field|NOCOMMAND
specifier|final
specifier|static
name|int
name|NOCOMMAND
init|=
operator|-
literal|2
decl_stmt|;
DECL|field|UNKOWN
specifier|final
specifier|static
name|int
name|UNKOWN
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|INFO
specifier|final
specifier|static
name|int
name|INFO
init|=
literal|0
decl_stmt|;
DECL|field|SEARCH
specifier|final
specifier|static
name|int
name|SEARCH
init|=
literal|1
decl_stmt|;
DECL|field|OPTIMIZE
specifier|final
specifier|static
name|int
name|OPTIMIZE
init|=
literal|2
decl_stmt|;
DECL|field|QUIT
specifier|final
specifier|static
name|int
name|QUIT
init|=
literal|3
decl_stmt|;
DECL|field|HELP
specifier|final
specifier|static
name|int
name|HELP
init|=
literal|4
decl_stmt|;
DECL|field|COUNT
specifier|final
specifier|static
name|int
name|COUNT
init|=
literal|5
decl_stmt|;
DECL|field|TERMS
specifier|final
specifier|static
name|int
name|TERMS
init|=
literal|6
decl_stmt|;
DECL|field|INDEX
specifier|final
specifier|static
name|int
name|INDEX
init|=
literal|7
decl_stmt|;
DECL|field|TOKENS
specifier|final
specifier|static
name|int
name|TOKENS
init|=
literal|8
decl_stmt|;
DECL|field|EXPLAIN
specifier|final
specifier|static
name|int
name|EXPLAIN
init|=
literal|9
decl_stmt|;
DECL|field|fullPath
name|String
name|fullPath
decl_stmt|;
DECL|field|commandMap
name|TreeMap
name|commandMap
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
DECL|field|luceneMethods
name|LuceneMethods
name|luceneMethods
decl_stmt|;
comment|//current cli class we're using
DECL|field|enableReadline
name|boolean
name|enableReadline
decl_stmt|;
comment|//false: use plain java. True: shared library readline
comment|/** 		Main entry point. The first argument can be a filename with an 		application initialization file. 		*/
DECL|method|Lucli
specifier|public
name|Lucli
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|ParseException
throws|,
name|IOException
block|{
name|String
name|line
decl_stmt|;
name|fullPath
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.home"
argument_list|)
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"file.separator"
argument_list|)
operator|+
name|HISTORYFILE
expr_stmt|;
comment|/* 		 * Initialize the list of commands 		 */
name|addCommand
argument_list|(
literal|"info"
argument_list|,
name|INFO
argument_list|,
literal|"Display info about the current Lucene Index. Example:info"
argument_list|)
expr_stmt|;
name|addCommand
argument_list|(
literal|"search"
argument_list|,
name|SEARCH
argument_list|,
literal|"Search the current index. Example: search foo"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|addCommand
argument_list|(
literal|"count"
argument_list|,
name|COUNT
argument_list|,
literal|"Return the number of hits for a search. Example: count foo"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|addCommand
argument_list|(
literal|"optimize"
argument_list|,
name|OPTIMIZE
argument_list|,
literal|"Optimize the current index"
argument_list|)
expr_stmt|;
name|addCommand
argument_list|(
literal|"quit"
argument_list|,
name|QUIT
argument_list|,
literal|"Quit/exit the program"
argument_list|)
expr_stmt|;
name|addCommand
argument_list|(
literal|"help"
argument_list|,
name|HELP
argument_list|,
literal|"Display help about commands."
argument_list|)
expr_stmt|;
name|addCommand
argument_list|(
literal|"terms"
argument_list|,
name|TERMS
argument_list|,
literal|"Show the first "
operator|+
name|MAX_TERMS
operator|+
literal|" terms in this index. Supply a field name to only show terms in a specific field. Example: terms"
argument_list|)
expr_stmt|;
name|addCommand
argument_list|(
literal|"index"
argument_list|,
name|INDEX
argument_list|,
literal|"Choose a different lucene index. Example index my_index"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|addCommand
argument_list|(
literal|"tokens"
argument_list|,
name|TOKENS
argument_list|,
literal|"Does a search and shows the top 10 tokens for each document. Verbose! Example: tokens foo"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|addCommand
argument_list|(
literal|"explain"
argument_list|,
name|EXPLAIN
argument_list|,
literal|"Explanation that describes how the document scored against query. Example: explain foo"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|//parse command line arguments
name|parseArgs
argument_list|(
name|args
argument_list|)
expr_stmt|;
if|if
condition|(
name|enableReadline
condition|)
name|org
operator|.
name|gnu
operator|.
name|readline
operator|.
name|Readline
operator|.
name|load
argument_list|(
name|ReadlineLibrary
operator|.
name|GnuReadline
argument_list|)
expr_stmt|;
else|else
name|org
operator|.
name|gnu
operator|.
name|readline
operator|.
name|Readline
operator|.
name|load
argument_list|(
name|ReadlineLibrary
operator|.
name|PureJava
argument_list|)
expr_stmt|;
name|Readline
operator|.
name|initReadline
argument_list|(
literal|"lucli"
argument_list|)
expr_stmt|;
comment|// init, set app name, read inputrc
name|Readline
operator|.
name|readHistoryFile
argument_list|(
name|fullPath
argument_list|)
expr_stmt|;
comment|// read history file, if available
name|File
name|history
init|=
operator|new
name|File
argument_list|(
literal|".rltest_history"
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|history
operator|.
name|exists
argument_list|()
condition|)
name|Readline
operator|.
name|readHistoryFile
argument_list|(
name|history
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Error reading history file!"
argument_list|)
expr_stmt|;
block|}
comment|// Set word break characters
try|try
block|{
name|Readline
operator|.
name|setWordBreakCharacters
argument_list|(
literal|" \t;"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|enc
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Could not set word break characters"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// set completer with list of words
name|Readline
operator|.
name|setCompleter
argument_list|(
operator|new
name|Completer
argument_list|(
name|commandMap
argument_list|)
argument_list|)
expr_stmt|;
comment|// main input loop
name|luceneMethods
operator|=
operator|new
name|LuceneMethods
argument_list|(
name|DEFAULT_INDEX
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|line
operator|=
name|Readline
operator|.
name|readline
argument_list|(
literal|"lucli> "
argument_list|)
expr_stmt|;
if|if
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
name|handleCommand
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|enc
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"caught UnsupportedEncodingException"
argument_list|)
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|io
operator|.
name|EOFException
name|eof
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
comment|//new line
name|exit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|ioe
operator|.
name|printStackTrace
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
block|}
block|}
name|exit
argument_list|()
expr_stmt|;
block|}
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|ParseException
throws|,
name|IOException
block|{
operator|new
name|Lucli
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
DECL|method|handleCommand
specifier|private
name|void
name|handleCommand
parameter_list|(
name|String
name|line
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|String
index|[]
name|words
init|=
name|tokenizeCommand
argument_list|(
name|line
argument_list|)
decl_stmt|;
if|if
condition|(
name|words
operator|.
name|length
operator|==
literal|0
condition|)
return|return;
comment|//white space
name|String
name|query
init|=
literal|""
decl_stmt|;
comment|//Command name and number of arguments
switch|switch
condition|(
name|getCommandId
argument_list|(
name|words
index|[
literal|0
index|]
argument_list|,
name|words
operator|.
name|length
operator|-
literal|1
argument_list|)
condition|)
block|{
case|case
name|INFO
case|:
name|luceneMethods
operator|.
name|info
argument_list|()
expr_stmt|;
break|break;
case|case
name|SEARCH
case|:
for|for
control|(
name|int
name|ii
init|=
literal|1
init|;
name|ii
operator|<
name|words
operator|.
name|length
condition|;
name|ii
operator|++
control|)
block|{
name|query
operator|+=
name|words
index|[
name|ii
index|]
operator|+
literal|" "
expr_stmt|;
block|}
name|luceneMethods
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
break|break;
case|case
name|COUNT
case|:
for|for
control|(
name|int
name|ii
init|=
literal|1
init|;
name|ii
operator|<
name|words
operator|.
name|length
condition|;
name|ii
operator|++
control|)
block|{
name|query
operator|+=
name|words
index|[
name|ii
index|]
operator|+
literal|" "
expr_stmt|;
block|}
name|luceneMethods
operator|.
name|count
argument_list|(
name|query
argument_list|)
expr_stmt|;
break|break;
case|case
name|QUIT
case|:
name|exit
argument_list|()
expr_stmt|;
break|break;
case|case
name|TERMS
case|:
if|if
condition|(
name|words
operator|.
name|length
operator|>
literal|1
condition|)
name|luceneMethods
operator|.
name|terms
argument_list|(
name|words
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
else|else
name|luceneMethods
operator|.
name|terms
argument_list|(
literal|null
argument_list|)
expr_stmt|;
break|break;
case|case
name|INDEX
case|:
name|LuceneMethods
name|newLm
init|=
operator|new
name|LuceneMethods
argument_list|(
name|words
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
try|try
block|{
name|newLm
operator|.
name|info
argument_list|()
expr_stmt|;
comment|//will fail if can't open the index
name|luceneMethods
operator|=
name|newLm
expr_stmt|;
comment|//OK, so we'll use the new one
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|//problem we'll keep using the old one
name|error
argument_list|(
name|ioe
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|OPTIMIZE
case|:
name|luceneMethods
operator|.
name|optimize
argument_list|()
expr_stmt|;
break|break;
case|case
name|TOKENS
case|:
for|for
control|(
name|int
name|ii
init|=
literal|1
init|;
name|ii
operator|<
name|words
operator|.
name|length
condition|;
name|ii
operator|++
control|)
block|{
name|query
operator|+=
name|words
index|[
name|ii
index|]
operator|+
literal|" "
expr_stmt|;
block|}
name|luceneMethods
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|EXPLAIN
case|:
for|for
control|(
name|int
name|ii
init|=
literal|1
init|;
name|ii
operator|<
name|words
operator|.
name|length
condition|;
name|ii
operator|++
control|)
block|{
name|query
operator|+=
name|words
index|[
name|ii
index|]
operator|+
literal|" "
expr_stmt|;
block|}
name|luceneMethods
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
break|break;
case|case
name|HELP
case|:
name|help
argument_list|()
expr_stmt|;
break|break;
case|case
name|NOCOMMAND
case|:
comment|//do nothing
break|break;
case|case
name|UNKOWN
case|:
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Unknown command:"
operator|+
name|words
index|[
literal|0
index|]
operator|+
literal|". Type help to get a list of commands."
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
DECL|method|tokenizeCommand
specifier|private
name|String
index|[]
name|tokenizeCommand
parameter_list|(
name|String
name|line
parameter_list|)
block|{
name|StringTokenizer
name|tokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|line
argument_list|,
literal|" \t"
argument_list|)
decl_stmt|;
name|int
name|size
init|=
name|tokenizer
operator|.
name|countTokens
argument_list|()
decl_stmt|;
name|String
index|[]
name|tokens
init|=
operator|new
name|String
index|[
name|size
index|]
decl_stmt|;
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|tokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|;
name|ii
operator|++
control|)
block|{
name|tokens
index|[
name|ii
index|]
operator|=
name|tokenizer
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
return|return
name|tokens
return|;
block|}
DECL|method|exit
specifier|private
name|void
name|exit
parameter_list|()
block|{
try|try
block|{
name|Readline
operator|.
name|writeHistoryFile
argument_list|(
name|fullPath
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|error
argument_list|(
literal|"while saving history:"
operator|+
name|ioe
argument_list|)
expr_stmt|;
block|}
name|Readline
operator|.
name|cleanup
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Add a command to the list of commands for the interpreter for a 	 * command that doesn't take any parameters. 	 * @param name  - the name of the command 	 * @param id  - the unique id of the command 	 * @param help  - the help message for this command 	 */
DECL|method|addCommand
specifier|private
name|void
name|addCommand
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|id
parameter_list|,
name|String
name|help
parameter_list|)
block|{
name|addCommand
argument_list|(
name|name
argument_list|,
name|id
argument_list|,
name|help
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Add a command to the list of commands for the interpreter. 	 * @param name  - the name of the command 	 * @param id  - the unique id of the command 	 * @param help  - the help message for this command 	 * @param params  - the minimum number of required params if any 	 */
DECL|method|addCommand
specifier|private
name|void
name|addCommand
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|id
parameter_list|,
name|String
name|help
parameter_list|,
name|int
name|params
parameter_list|)
block|{
name|Command
name|command
init|=
operator|new
name|Command
argument_list|(
name|name
argument_list|,
name|id
argument_list|,
name|help
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|commandMap
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|command
argument_list|)
expr_stmt|;
block|}
DECL|method|getCommandId
specifier|private
name|int
name|getCommandId
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|params
parameter_list|)
block|{
name|name
operator|.
name|toLowerCase
argument_list|()
expr_stmt|;
comment|//treat uppercase and lower case commands the same
name|Command
name|command
init|=
operator|(
name|Command
operator|)
name|commandMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|command
operator|==
literal|null
condition|)
block|{
return|return
operator|(
name|UNKOWN
operator|)
return|;
block|}
else|else
block|{
if|if
condition|(
name|command
operator|.
name|params
operator|>
name|params
condition|)
block|{
name|error
argument_list|(
name|command
operator|.
name|name
operator|+
literal|" needs at least "
operator|+
name|command
operator|.
name|params
operator|+
literal|" arguments."
argument_list|)
expr_stmt|;
return|return
operator|(
name|NOCOMMAND
operator|)
return|;
block|}
return|return
operator|(
name|command
operator|.
name|id
operator|)
return|;
block|}
block|}
DECL|method|help
specifier|private
name|void
name|help
parameter_list|()
block|{
name|Iterator
name|commands
init|=
name|commandMap
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|commands
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Command
name|command
init|=
operator|(
name|Command
operator|)
name|commandMap
operator|.
name|get
argument_list|(
name|commands
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\t"
operator|+
name|command
operator|.
name|name
operator|+
literal|": "
operator|+
name|command
operator|.
name|help
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|error
specifier|private
name|void
name|error
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Error:"
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|message
specifier|private
name|void
name|message
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
comment|/* 	 * Parse command line arguments 	 * Code inspired by http://www.ecs.umass.edu/ece/wireless/people/emmanuel/java/java/cmdLineArgs/parsing.html 	 */
DECL|method|parseArgs
specifier|private
name|void
name|parseArgs
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
name|args
operator|.
name|length
condition|;
name|ii
operator|++
control|)
block|{
comment|// a little overkill for now, but foundation
comment|// for other args
if|if
condition|(
name|args
index|[
name|ii
index|]
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|String
name|arg
init|=
name|args
index|[
name|ii
index|]
decl_stmt|;
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"-r"
argument_list|)
condition|)
block|{
name|enableReadline
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|usage
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|usage
specifier|private
name|void
name|usage
parameter_list|()
block|{
name|message
argument_list|(
literal|"Usage: lucli [-r]"
argument_list|)
expr_stmt|;
name|message
argument_list|(
literal|"Arguments:"
argument_list|)
expr_stmt|;
name|message
argument_list|(
literal|"\t-r: Provide tab completion and history using the GNU readline shared library "
argument_list|)
expr_stmt|;
block|}
DECL|class|Command
specifier|private
class|class
name|Command
block|{
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|id
name|int
name|id
decl_stmt|;
DECL|field|numberArgs
name|int
name|numberArgs
decl_stmt|;
DECL|field|help
name|String
name|help
decl_stmt|;
DECL|field|params
name|int
name|params
decl_stmt|;
DECL|method|Command
name|Command
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|id
parameter_list|,
name|String
name|help
parameter_list|,
name|int
name|params
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|help
operator|=
name|help
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
block|}
comment|/** 		 * Prints out a usage message for this command. 		 */
DECL|method|commandUsage
specifier|public
name|String
name|commandUsage
parameter_list|()
block|{
return|return
operator|(
name|name
operator|+
literal|":"
operator|+
name|help
operator|+
literal|". Command takes "
operator|+
name|params
operator|+
literal|" params"
operator|)
return|;
block|}
block|}
block|}
end_class

end_unit

