begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// This file was generated automatically by the Snowball to Java compiler
end_comment

begin_package
DECL|package|org.tartarus.snowball.ext
package|package
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|ext
package|;
end_package

begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|Among
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|SnowballProgram
import|;
end_import

begin_comment
comment|/**   * This class was automatically generated by a Snowball to Java compiler    * It implements the stemming algorithm defined by a snowball script.   */
end_comment

begin_class
DECL|class|IrishStemmer
specifier|public
class|class
name|IrishStemmer
extends|extends
name|SnowballProgram
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|field|methodObject
specifier|private
specifier|final
specifier|static
name|IrishStemmer
name|methodObject
init|=
operator|new
name|IrishStemmer
argument_list|()
decl_stmt|;
DECL|field|a_0
specifier|private
specifier|final
specifier|static
name|Among
name|a_0
index|[]
init|=
block|{
operator|new
name|Among
argument_list|(
literal|"b'"
argument_list|,
operator|-
literal|1
argument_list|,
literal|4
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"bh"
argument_list|,
operator|-
literal|1
argument_list|,
literal|14
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"bhf"
argument_list|,
literal|1
argument_list|,
literal|9
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"bp"
argument_list|,
operator|-
literal|1
argument_list|,
literal|11
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ch"
argument_list|,
operator|-
literal|1
argument_list|,
literal|15
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"d'"
argument_list|,
operator|-
literal|1
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"d'fh"
argument_list|,
literal|5
argument_list|,
literal|3
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"dh"
argument_list|,
operator|-
literal|1
argument_list|,
literal|16
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"dt"
argument_list|,
operator|-
literal|1
argument_list|,
literal|13
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"fh"
argument_list|,
operator|-
literal|1
argument_list|,
literal|17
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"gc"
argument_list|,
operator|-
literal|1
argument_list|,
literal|7
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"gh"
argument_list|,
operator|-
literal|1
argument_list|,
literal|18
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"h-"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"m'"
argument_list|,
operator|-
literal|1
argument_list|,
literal|4
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"mb"
argument_list|,
operator|-
literal|1
argument_list|,
literal|6
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"mh"
argument_list|,
operator|-
literal|1
argument_list|,
literal|19
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"n-"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"nd"
argument_list|,
operator|-
literal|1
argument_list|,
literal|8
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ng"
argument_list|,
operator|-
literal|1
argument_list|,
literal|10
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ph"
argument_list|,
operator|-
literal|1
argument_list|,
literal|20
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"sh"
argument_list|,
operator|-
literal|1
argument_list|,
literal|5
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"t-"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"th"
argument_list|,
operator|-
literal|1
argument_list|,
literal|21
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ts"
argument_list|,
operator|-
literal|1
argument_list|,
literal|12
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|}
decl_stmt|;
DECL|field|a_1
specifier|private
specifier|final
specifier|static
name|Among
name|a_1
index|[]
init|=
block|{
operator|new
name|Among
argument_list|(
literal|"\u00EDochta"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"a\u00EDochta"
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ire"
argument_list|,
operator|-
literal|1
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"aire"
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"abh"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"eabh"
argument_list|,
literal|4
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ibh"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"aibh"
argument_list|,
literal|6
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"amh"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"eamh"
argument_list|,
literal|8
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"imh"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"aimh"
argument_list|,
literal|10
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"\u00EDocht"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"a\u00EDocht"
argument_list|,
literal|12
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ir\u00ED"
argument_list|,
operator|-
literal|1
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"air\u00ED"
argument_list|,
literal|14
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|}
decl_stmt|;
DECL|field|a_2
specifier|private
specifier|final
specifier|static
name|Among
name|a_2
index|[]
init|=
block|{
operator|new
name|Among
argument_list|(
literal|"\u00F3ideacha"
argument_list|,
operator|-
literal|1
argument_list|,
literal|6
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"patacha"
argument_list|,
operator|-
literal|1
argument_list|,
literal|5
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"achta"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"arcachta"
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"eachta"
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"grafa\u00EDochta"
argument_list|,
operator|-
literal|1
argument_list|,
literal|4
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"paite"
argument_list|,
operator|-
literal|1
argument_list|,
literal|5
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ach"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"each"
argument_list|,
literal|7
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"\u00F3ideach"
argument_list|,
literal|8
argument_list|,
literal|6
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"gineach"
argument_list|,
literal|8
argument_list|,
literal|3
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"patach"
argument_list|,
literal|7
argument_list|,
literal|5
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"grafa\u00EDoch"
argument_list|,
operator|-
literal|1
argument_list|,
literal|4
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"pataigh"
argument_list|,
operator|-
literal|1
argument_list|,
literal|5
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"\u00F3idigh"
argument_list|,
operator|-
literal|1
argument_list|,
literal|6
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"acht\u00FAil"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"eacht\u00FAil"
argument_list|,
literal|15
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"gineas"
argument_list|,
operator|-
literal|1
argument_list|,
literal|3
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ginis"
argument_list|,
operator|-
literal|1
argument_list|,
literal|3
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"acht"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"arcacht"
argument_list|,
literal|19
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"eacht"
argument_list|,
literal|19
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"grafa\u00EDocht"
argument_list|,
operator|-
literal|1
argument_list|,
literal|4
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"arcachta\u00ED"
argument_list|,
operator|-
literal|1
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"grafa\u00EDochta\u00ED"
argument_list|,
operator|-
literal|1
argument_list|,
literal|4
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|}
decl_stmt|;
DECL|field|a_3
specifier|private
specifier|final
specifier|static
name|Among
name|a_3
index|[]
init|=
block|{
operator|new
name|Among
argument_list|(
literal|"imid"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"aimid"
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"\u00EDmid"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"a\u00EDmid"
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"adh"
argument_list|,
operator|-
literal|1
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"eadh"
argument_list|,
literal|4
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"faidh"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"fidh"
argument_list|,
operator|-
literal|1
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"\u00E1il"
argument_list|,
operator|-
literal|1
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"ain"
argument_list|,
operator|-
literal|1
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"tear"
argument_list|,
operator|-
literal|1
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|,
operator|new
name|Among
argument_list|(
literal|"tar"
argument_list|,
operator|-
literal|1
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|,
name|methodObject
argument_list|)
block|}
decl_stmt|;
DECL|field|g_v
specifier|private
specifier|static
specifier|final
name|char
name|g_v
index|[]
init|=
block|{
literal|17
block|,
literal|65
block|,
literal|16
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|1
block|,
literal|17
block|,
literal|4
block|,
literal|2
block|}
decl_stmt|;
DECL|field|I_p2
specifier|private
name|int
name|I_p2
decl_stmt|;
DECL|field|I_p1
specifier|private
name|int
name|I_p1
decl_stmt|;
DECL|field|I_pV
specifier|private
name|int
name|I_pV
decl_stmt|;
DECL|method|copy_from
specifier|private
name|void
name|copy_from
parameter_list|(
name|IrishStemmer
name|other
parameter_list|)
block|{
name|I_p2
operator|=
name|other
operator|.
name|I_p2
expr_stmt|;
name|I_p1
operator|=
name|other
operator|.
name|I_p1
expr_stmt|;
name|I_pV
operator|=
name|other
operator|.
name|I_pV
expr_stmt|;
name|super
operator|.
name|copy_from
argument_list|(
name|other
argument_list|)
expr_stmt|;
block|}
DECL|method|r_mark_regions
specifier|private
name|boolean
name|r_mark_regions
parameter_list|()
block|{
name|int
name|v_1
decl_stmt|;
name|int
name|v_3
decl_stmt|;
comment|// (, line 28
name|I_pV
operator|=
name|limit
expr_stmt|;
name|I_p1
operator|=
name|limit
expr_stmt|;
name|I_p2
operator|=
name|limit
expr_stmt|;
comment|// do, line 34
name|v_1
operator|=
name|cursor
expr_stmt|;
name|lab0
label|:
do|do
block|{
comment|// (, line 34
comment|// gopast, line 35
name|golab1
label|:
while|while
condition|(
literal|true
condition|)
block|{
name|lab2
label|:
do|do
block|{
if|if
condition|(
operator|!
operator|(
name|in_grouping
argument_list|(
name|g_v
argument_list|,
literal|97
argument_list|,
literal|250
argument_list|)
operator|)
condition|)
block|{
break|break
name|lab2
break|;
block|}
break|break
name|golab1
break|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
if|if
condition|(
name|cursor
operator|>=
name|limit
condition|)
block|{
break|break
name|lab0
break|;
block|}
name|cursor
operator|++
expr_stmt|;
block|}
comment|// setmark pV, line 35
name|I_pV
operator|=
name|cursor
expr_stmt|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|v_1
expr_stmt|;
comment|// do, line 37
name|v_3
operator|=
name|cursor
expr_stmt|;
name|lab3
label|:
do|do
block|{
comment|// (, line 37
comment|// gopast, line 38
name|golab4
label|:
while|while
condition|(
literal|true
condition|)
block|{
name|lab5
label|:
do|do
block|{
if|if
condition|(
operator|!
operator|(
name|in_grouping
argument_list|(
name|g_v
argument_list|,
literal|97
argument_list|,
literal|250
argument_list|)
operator|)
condition|)
block|{
break|break
name|lab5
break|;
block|}
break|break
name|golab4
break|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
if|if
condition|(
name|cursor
operator|>=
name|limit
condition|)
block|{
break|break
name|lab3
break|;
block|}
name|cursor
operator|++
expr_stmt|;
block|}
comment|// gopast, line 38
name|golab6
label|:
while|while
condition|(
literal|true
condition|)
block|{
name|lab7
label|:
do|do
block|{
if|if
condition|(
operator|!
operator|(
name|out_grouping
argument_list|(
name|g_v
argument_list|,
literal|97
argument_list|,
literal|250
argument_list|)
operator|)
condition|)
block|{
break|break
name|lab7
break|;
block|}
break|break
name|golab6
break|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
if|if
condition|(
name|cursor
operator|>=
name|limit
condition|)
block|{
break|break
name|lab3
break|;
block|}
name|cursor
operator|++
expr_stmt|;
block|}
comment|// setmark p1, line 38
name|I_p1
operator|=
name|cursor
expr_stmt|;
comment|// gopast, line 39
name|golab8
label|:
while|while
condition|(
literal|true
condition|)
block|{
name|lab9
label|:
do|do
block|{
if|if
condition|(
operator|!
operator|(
name|in_grouping
argument_list|(
name|g_v
argument_list|,
literal|97
argument_list|,
literal|250
argument_list|)
operator|)
condition|)
block|{
break|break
name|lab9
break|;
block|}
break|break
name|golab8
break|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
if|if
condition|(
name|cursor
operator|>=
name|limit
condition|)
block|{
break|break
name|lab3
break|;
block|}
name|cursor
operator|++
expr_stmt|;
block|}
comment|// gopast, line 39
name|golab10
label|:
while|while
condition|(
literal|true
condition|)
block|{
name|lab11
label|:
do|do
block|{
if|if
condition|(
operator|!
operator|(
name|out_grouping
argument_list|(
name|g_v
argument_list|,
literal|97
argument_list|,
literal|250
argument_list|)
operator|)
condition|)
block|{
break|break
name|lab11
break|;
block|}
break|break
name|golab10
break|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
if|if
condition|(
name|cursor
operator|>=
name|limit
condition|)
block|{
break|break
name|lab3
break|;
block|}
name|cursor
operator|++
expr_stmt|;
block|}
comment|// setmark p2, line 39
name|I_p2
operator|=
name|cursor
expr_stmt|;
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|v_3
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|r_initial_morph
specifier|private
name|boolean
name|r_initial_morph
parameter_list|()
block|{
name|int
name|among_var
decl_stmt|;
comment|// (, line 43
comment|// [, line 44
name|bra
operator|=
name|cursor
expr_stmt|;
comment|// substring, line 44
name|among_var
operator|=
name|find_among
argument_list|(
name|a_0
argument_list|,
literal|24
argument_list|)
expr_stmt|;
if|if
condition|(
name|among_var
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// ], line 44
name|ket
operator|=
name|cursor
expr_stmt|;
switch|switch
condition|(
name|among_var
condition|)
block|{
case|case
literal|0
case|:
return|return
literal|false
return|;
case|case
literal|1
case|:
comment|// (, line 46
comment|// delete, line 46
name|slice_del
argument_list|()
expr_stmt|;
break|break;
case|case
literal|2
case|:
comment|// (, line 50
comment|// delete, line 50
name|slice_del
argument_list|()
expr_stmt|;
break|break;
case|case
literal|3
case|:
comment|// (, line 52
comment|//<-, line 52
name|slice_from
argument_list|(
literal|"f"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|4
case|:
comment|// (, line 55
comment|// delete, line 55
name|slice_del
argument_list|()
expr_stmt|;
break|break;
case|case
literal|5
case|:
comment|// (, line 58
comment|//<-, line 58
name|slice_from
argument_list|(
literal|"s"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|6
case|:
comment|// (, line 61
comment|//<-, line 61
name|slice_from
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|7
case|:
comment|// (, line 63
comment|//<-, line 63
name|slice_from
argument_list|(
literal|"c"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|8
case|:
comment|// (, line 65
comment|//<-, line 65
name|slice_from
argument_list|(
literal|"d"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|9
case|:
comment|// (, line 67
comment|//<-, line 67
name|slice_from
argument_list|(
literal|"f"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|10
case|:
comment|// (, line 69
comment|//<-, line 69
name|slice_from
argument_list|(
literal|"g"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|11
case|:
comment|// (, line 71
comment|//<-, line 71
name|slice_from
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|12
case|:
comment|// (, line 73
comment|//<-, line 73
name|slice_from
argument_list|(
literal|"s"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|13
case|:
comment|// (, line 75
comment|//<-, line 75
name|slice_from
argument_list|(
literal|"t"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|14
case|:
comment|// (, line 79
comment|//<-, line 79
name|slice_from
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|15
case|:
comment|// (, line 81
comment|//<-, line 81
name|slice_from
argument_list|(
literal|"c"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|16
case|:
comment|// (, line 83
comment|//<-, line 83
name|slice_from
argument_list|(
literal|"d"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|17
case|:
comment|// (, line 85
comment|//<-, line 85
name|slice_from
argument_list|(
literal|"f"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|18
case|:
comment|// (, line 87
comment|//<-, line 87
name|slice_from
argument_list|(
literal|"g"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|19
case|:
comment|// (, line 89
comment|//<-, line 89
name|slice_from
argument_list|(
literal|"m"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|20
case|:
comment|// (, line 91
comment|//<-, line 91
name|slice_from
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|21
case|:
comment|// (, line 93
comment|//<-, line 93
name|slice_from
argument_list|(
literal|"t"
argument_list|)
expr_stmt|;
break|break;
block|}
return|return
literal|true
return|;
block|}
DECL|method|r_RV
specifier|private
name|boolean
name|r_RV
parameter_list|()
block|{
if|if
condition|(
operator|!
operator|(
name|I_pV
operator|<=
name|cursor
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|r_R1
specifier|private
name|boolean
name|r_R1
parameter_list|()
block|{
if|if
condition|(
operator|!
operator|(
name|I_p1
operator|<=
name|cursor
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|r_R2
specifier|private
name|boolean
name|r_R2
parameter_list|()
block|{
if|if
condition|(
operator|!
operator|(
name|I_p2
operator|<=
name|cursor
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|r_noun_sfx
specifier|private
name|boolean
name|r_noun_sfx
parameter_list|()
block|{
name|int
name|among_var
decl_stmt|;
comment|// (, line 103
comment|// [, line 104
name|ket
operator|=
name|cursor
expr_stmt|;
comment|// substring, line 104
name|among_var
operator|=
name|find_among_b
argument_list|(
name|a_1
argument_list|,
literal|16
argument_list|)
expr_stmt|;
if|if
condition|(
name|among_var
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// ], line 104
name|bra
operator|=
name|cursor
expr_stmt|;
switch|switch
condition|(
name|among_var
condition|)
block|{
case|case
literal|0
case|:
return|return
literal|false
return|;
case|case
literal|1
case|:
comment|// (, line 108
comment|// call R1, line 108
if|if
condition|(
operator|!
name|r_R1
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// delete, line 108
name|slice_del
argument_list|()
expr_stmt|;
break|break;
case|case
literal|2
case|:
comment|// (, line 110
comment|// call R2, line 110
if|if
condition|(
operator|!
name|r_R2
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// delete, line 110
name|slice_del
argument_list|()
expr_stmt|;
break|break;
block|}
return|return
literal|true
return|;
block|}
DECL|method|r_deriv
specifier|private
name|boolean
name|r_deriv
parameter_list|()
block|{
name|int
name|among_var
decl_stmt|;
comment|// (, line 113
comment|// [, line 114
name|ket
operator|=
name|cursor
expr_stmt|;
comment|// substring, line 114
name|among_var
operator|=
name|find_among_b
argument_list|(
name|a_2
argument_list|,
literal|25
argument_list|)
expr_stmt|;
if|if
condition|(
name|among_var
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// ], line 114
name|bra
operator|=
name|cursor
expr_stmt|;
switch|switch
condition|(
name|among_var
condition|)
block|{
case|case
literal|0
case|:
return|return
literal|false
return|;
case|case
literal|1
case|:
comment|// (, line 116
comment|// call R2, line 116
if|if
condition|(
operator|!
name|r_R2
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// delete, line 116
name|slice_del
argument_list|()
expr_stmt|;
break|break;
case|case
literal|2
case|:
comment|// (, line 118
comment|//<-, line 118
name|slice_from
argument_list|(
literal|"arc"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|3
case|:
comment|// (, line 120
comment|//<-, line 120
name|slice_from
argument_list|(
literal|"gin"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|4
case|:
comment|// (, line 122
comment|//<-, line 122
name|slice_from
argument_list|(
literal|"graf"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|5
case|:
comment|// (, line 124
comment|//<-, line 124
name|slice_from
argument_list|(
literal|"paite"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|6
case|:
comment|// (, line 126
comment|//<-, line 126
name|slice_from
argument_list|(
literal|"\u00F3id"
argument_list|)
expr_stmt|;
break|break;
block|}
return|return
literal|true
return|;
block|}
DECL|method|r_verb_sfx
specifier|private
name|boolean
name|r_verb_sfx
parameter_list|()
block|{
name|int
name|among_var
decl_stmt|;
comment|// (, line 129
comment|// [, line 130
name|ket
operator|=
name|cursor
expr_stmt|;
comment|// substring, line 130
name|among_var
operator|=
name|find_among_b
argument_list|(
name|a_3
argument_list|,
literal|12
argument_list|)
expr_stmt|;
if|if
condition|(
name|among_var
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// ], line 130
name|bra
operator|=
name|cursor
expr_stmt|;
switch|switch
condition|(
name|among_var
condition|)
block|{
case|case
literal|0
case|:
return|return
literal|false
return|;
case|case
literal|1
case|:
comment|// (, line 133
comment|// call RV, line 133
if|if
condition|(
operator|!
name|r_RV
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// delete, line 133
name|slice_del
argument_list|()
expr_stmt|;
break|break;
case|case
literal|2
case|:
comment|// (, line 138
comment|// call R1, line 138
if|if
condition|(
operator|!
name|r_R1
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// delete, line 138
name|slice_del
argument_list|()
expr_stmt|;
break|break;
block|}
return|return
literal|true
return|;
block|}
DECL|method|stem
specifier|public
name|boolean
name|stem
parameter_list|()
block|{
name|int
name|v_1
decl_stmt|;
name|int
name|v_2
decl_stmt|;
name|int
name|v_3
decl_stmt|;
name|int
name|v_4
decl_stmt|;
name|int
name|v_5
decl_stmt|;
comment|// (, line 143
comment|// do, line 144
name|v_1
operator|=
name|cursor
expr_stmt|;
name|lab0
label|:
do|do
block|{
comment|// call initial_morph, line 144
if|if
condition|(
operator|!
name|r_initial_morph
argument_list|()
condition|)
block|{
break|break
name|lab0
break|;
block|}
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|v_1
expr_stmt|;
comment|// do, line 145
name|v_2
operator|=
name|cursor
expr_stmt|;
name|lab1
label|:
do|do
block|{
comment|// call mark_regions, line 145
if|if
condition|(
operator|!
name|r_mark_regions
argument_list|()
condition|)
block|{
break|break
name|lab1
break|;
block|}
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|v_2
expr_stmt|;
comment|// backwards, line 146
name|limit_backward
operator|=
name|cursor
expr_stmt|;
name|cursor
operator|=
name|limit
expr_stmt|;
comment|// (, line 146
comment|// do, line 147
name|v_3
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
name|lab2
label|:
do|do
block|{
comment|// call noun_sfx, line 147
if|if
condition|(
operator|!
name|r_noun_sfx
argument_list|()
condition|)
block|{
break|break
name|lab2
break|;
block|}
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|limit
operator|-
name|v_3
expr_stmt|;
comment|// do, line 148
name|v_4
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
name|lab3
label|:
do|do
block|{
comment|// call deriv, line 148
if|if
condition|(
operator|!
name|r_deriv
argument_list|()
condition|)
block|{
break|break
name|lab3
break|;
block|}
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|limit
operator|-
name|v_4
expr_stmt|;
comment|// do, line 149
name|v_5
operator|=
name|limit
operator|-
name|cursor
expr_stmt|;
name|lab4
label|:
do|do
block|{
comment|// call verb_sfx, line 149
if|if
condition|(
operator|!
name|r_verb_sfx
argument_list|()
condition|)
block|{
break|break
name|lab4
break|;
block|}
block|}
do|while
condition|(
literal|false
condition|)
do|;
name|cursor
operator|=
name|limit
operator|-
name|v_5
expr_stmt|;
name|cursor
operator|=
name|limit_backward
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|o
operator|instanceof
name|IrishStemmer
return|;
block|}
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|IrishStemmer
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

