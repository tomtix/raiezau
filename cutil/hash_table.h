#ifndef HASH_TABLE_H
#define HASH_TABLE_H

#include <stdlib.h>

struct hash_table;

struct hash_table* ht_create(size_t size, int (*hash)(const char*));

int ht_entry_count(const struct hash_table *h);
int ht_add_entry(struct hash_table* ht, const char *key, void *data);
int ht_add_unique_entry(struct hash_table *h, const char *key, void *data);
int ht_remove_entry(struct hash_table *ht, const char *key);
int ht_has_entry(struct hash_table *ht, const char *key);
int ht_get_entry(struct hash_table *ht, const char *key, void *ret);
int ht_hash(struct hash_table *ht, const char *key);
void ht_free(struct hash_table* ht);
void ht_for_each(struct hash_table* ht,
		 void (*fun)(const char *, void*, void*), void *args);
struct list* ht_to_list(struct hash_table *ht);

#endif //HASH_TABLE_H
