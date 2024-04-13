# How hard can it be to roll my own note taking system?

I like to build the tools I use.
I also like my website, [play.teod.eu].

[play.teod.eu]: https://play.teod.eu/

In a sense, my website is my note taking system.
But there are gaps.

1. I can only work with my website from a computer with Emacs, Pandoc, Git, Make, Clojure and Babashka
2. The UX for tagging pages is poor
3. The UX for "capturing notes" is poor.

I don't think I'll "just fix all of these by writing some code".
But I'd like to at least try to address some of these concerns.

## Deploying to application.garden

I want to try use [application.garden] for something.
Why?
It seems to handle the impure parts of application deployment in a neater way than I've seen elsewhere.
Let's figure out if that assumption is correct in practice.

[application.garden]: https://application.garden/

## Multi-user data model

Rough idea:

1. lean on garden's login system
2. store notes on disk (text files by uuid under user id)

Operations:

1. List my notes
2. Edit a note
3. Save changes
4. Create a new note

"multi-editing and groups?"
Let's not start there.
`garden groups` looks interesting, but let's not make this too hard on ourselves.
